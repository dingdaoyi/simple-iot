{{/* Common labels */}}
{{- define "simple-iot.labels" -}}
app.kubernetes.io/name: simple-iot
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end -}}

{{/* Image with optional registry prefix */}}
{{- define "simple-iot.image" -}}
{{- $img := .image -}}
{{- if .registry -}}{{- printf "%s/%s" .registry $img -}}{{- else -}}{{- $img -}}{{- end -}}
{{- end -}}
