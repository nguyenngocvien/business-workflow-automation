let onUnauthorized: (() => void) | null = null;

export function setUnauthorizedHandler(handler: () => void) {
    onUnauthorized = handler;
}

export function handleUnauthorized() {
    if (onUnauthorized) {
        onUnauthorized();
    }
}