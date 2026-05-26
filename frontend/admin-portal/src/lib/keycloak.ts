import {
  clearStoredSession,
  getStoredAccessToken as getAccessTokenFromStore,
  getStoredSession as getSessionFromStore,
  setStoredSession,
} from '../stores/authStore';

const PKCE_STORAGE_KEY = 'admin-dashboard-keycloak-pkce';
const POST_LOGIN_REDIRECT_KEY = 'admin-dashboard-post-login-redirect';

type JwtPayload = {
  name?: string;
  preferred_username?: string;
  email?: string;
  given_name?: string;
  family_name?: string;
};

type KeycloakConfig = {
  baseUrl: string;
  realm: string;
  clientId: string;
  redirectUri: string;
  logoutRedirectUri: string;
};

type PkceState = {
  state: string;
  codeVerifier: string;
};

function getAppBasePath() {
  return import.meta.env.BASE_URL.replace(/\/$/, '');
}

function getRedirectUri(path: string) {
  return `${window.location.origin}${getAppBasePath()}${path}`;
}

function readConfig(): KeycloakConfig {
  const baseUrl = import.meta.env.VITE_KEYCLOAK_URL?.trim();
  const realm = import.meta.env.VITE_KEYCLOAK_REALM?.trim();
  const clientId = import.meta.env.VITE_KEYCLOAK_CLIENT_ID?.trim();

  if (!baseUrl || !realm || !clientId) {
    throw new Error('Keycloak is not configured. Set VITE_KEYCLOAK_URL, VITE_KEYCLOAK_REALM, and VITE_KEYCLOAK_CLIENT_ID.');
  }

  return {
    baseUrl: baseUrl.replace(/\/$/, ''),
    realm,
    clientId,
    redirectUri: import.meta.env.VITE_KEYCLOAK_REDIRECT_URI?.trim() || getRedirectUri('/login'),
    logoutRedirectUri: import.meta.env.VITE_KEYCLOAK_LOGOUT_REDIRECT_URI?.trim() || getRedirectUri('/login'),
  };
}

function safeJsonParse<T>(value: string | null) {
  if (!value) {
    return null;
  }

  try {
    return JSON.parse(value) as T;
  } catch {
    return null;
  }
}

function base64UrlEncode(buffer: ArrayBuffer) {
  const bytes = new Uint8Array(buffer);
  let binary = '';

  for (const byte of bytes) {
    binary += String.fromCharCode(byte);
  }

  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
}

function randomString(length = 64) {
  const array = new Uint8Array(length);
  crypto.getRandomValues(array);
  return Array.from(array, (value) => value.toString(16).padStart(2, '0')).join('');
}

export function getStoredAccessToken() {
  if (typeof window === 'undefined') {
    return null;
  }

  return getAccessTokenFromStore();
}

export function getStoredAuthUser() {
  if (typeof window === 'undefined') {
    return null;
  }

  return getSessionFromStore()?.user ?? null;
}

export function getPostLoginRedirect() {
  if (typeof window === 'undefined') {
    return '/';
  }

  return window.sessionStorage.getItem(POST_LOGIN_REDIRECT_KEY) || '/';
}

export function setPostLoginRedirect(path: string) {
  if (typeof window === 'undefined') {
    return;
  }

  window.sessionStorage.setItem(POST_LOGIN_REDIRECT_KEY, path || '/');
}

export function clearPostLoginRedirect() {
  if (typeof window === 'undefined') {
    return;
  }

  window.sessionStorage.removeItem(POST_LOGIN_REDIRECT_KEY);
}

async function createCodeChallenge(codeVerifier: string) {
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(codeVerifier));
  return base64UrlEncode(digest);
}

export async function startKeycloakLogin() {
  if (typeof window === 'undefined') {
    throw new Error('Keycloak login can only run in the browser.');
  }

  const config = readConfig();
  const state = randomString(32);
  const codeVerifier = randomString(64);
  const codeChallenge = await createCodeChallenge(codeVerifier);

  const pkceState: PkceState = { state, codeVerifier };

  window.sessionStorage.setItem(PKCE_STORAGE_KEY, JSON.stringify(pkceState));

  const authUrl = new URL(`${config.baseUrl}/realms/${config.realm}/protocol/openid-connect/auth`);
  authUrl.searchParams.set('client_id', config.clientId);
  authUrl.searchParams.set('redirect_uri', config.redirectUri);
  authUrl.searchParams.set('response_type', 'code');
  authUrl.searchParams.set('scope', 'openid profile email');
  authUrl.searchParams.set('state', state);
  authUrl.searchParams.set('code_challenge', codeChallenge);
  authUrl.searchParams.set('code_challenge_method', 'S256');

  window.location.assign(authUrl.toString());
}

async function exchangeCodeForTokens(code: string, codeVerifier: string) {
  const config = readConfig();
  const tokenUrl = `${config.baseUrl}/realms/${config.realm}/protocol/openid-connect/token`;
  const body = new URLSearchParams({
    client_id: config.clientId,
    grant_type: 'authorization_code',
    code,
    redirect_uri: config.redirectUri,
    code_verifier: codeVerifier,
  });

  const response = await fetch(tokenUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Accept: 'application/json',
    },
    body,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || 'Failed to exchange authorization code for tokens.');
  }

  return response.json() as Promise<{
    access_token: string;
    refresh_token?: string;
    id_token?: string;
    expires_in: number;
    token_type: string;
  }>;
}

export async function completeKeycloakLoginFromCallback(searchParams: URLSearchParams) {
  if (typeof window === 'undefined') {
    throw new Error('Keycloak callback can only run in the browser.');
  }

  const error = searchParams.get('error');
  if (error) {
    const description = searchParams.get('error_description');
    throw new Error(description || error);
  }

  const code = searchParams.get('code');
  const state = searchParams.get('state');
  const pkceState = safeJsonParse<PkceState>(window.sessionStorage.getItem(PKCE_STORAGE_KEY));

  if (!code || !state || !pkceState) {
    throw new Error('Missing authorization code or PKCE state.');
  }

  if (pkceState.state !== state) {
    throw new Error('Invalid Keycloak state.');
  }

  const tokens = await exchangeCodeForTokens(code, pkceState.codeVerifier);
  window.sessionStorage.removeItem(PKCE_STORAGE_KEY);

  const claims = decodeJwtPayload(tokens.id_token ?? tokens.access_token);
  const user = {
    name: claims?.name ?? claims?.preferred_username ?? claims?.email ?? 'Authenticated user',
    email: claims?.email ?? claims?.preferred_username ?? '',
  };

  setStoredSession({
    accessToken: tokens.access_token,
    refreshToken: tokens.refresh_token,
    idToken: tokens.id_token,
    expiresAt: Date.now() + tokens.expires_in * 1000,
    tokenType: tokens.token_type,
    user,
  });

  return {
    user,
    redirectTo: getPostLoginRedirect(),
  };
}

export function decodeJwtPayload(token: string) {
  const payload = token.split('.')[1];
  if (!payload) {
    return null;
  }

  const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
  const base64 = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
  const json = atob(base64);
  return JSON.parse(json) as JwtPayload;
}

export function getStoredSession() {
  if (typeof window === 'undefined') {
    return null;
  }

  return getSessionFromStore();
}

export function clearKeycloakLoginArtifacts() {
  if (typeof window === 'undefined') {
    return;
  }

  window.sessionStorage.removeItem(PKCE_STORAGE_KEY);
  clearPostLoginRedirect();
}

export function clearAuthenticatedState() {
  clearStoredSession();
  clearKeycloakLoginArtifacts();
}

export function buildKeycloakLogoutUrl(idTokenHint?: string) {
  if (typeof window === 'undefined') {
    throw new Error('Keycloak logout can only run in the browser.');
  }

  const config = readConfig();
  const logoutUrl = new URL(`${config.baseUrl}/realms/${config.realm}/protocol/openid-connect/logout`);

  if (idTokenHint) {
    logoutUrl.searchParams.set('id_token_hint', idTokenHint);
  }

  logoutUrl.searchParams.set('client_id', config.clientId);
  logoutUrl.searchParams.set('post_logout_redirect_uri', config.logoutRedirectUri);

  return logoutUrl.toString();
}
