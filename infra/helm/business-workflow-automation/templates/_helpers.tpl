{{- define "business-workflow-automation.name" -}}
business-workflow-automation
{{- end -}}

{{- define "business-workflow-automation.fullname" -}}
{{- printf "%s" (include "business-workflow-automation.name" .) -}}
{{- end -}}
