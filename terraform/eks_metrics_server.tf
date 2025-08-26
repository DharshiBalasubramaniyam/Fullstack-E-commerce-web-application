# helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
# helm upgrade --install metrics-server metrics-server/metrics-server

resource "helm_release" "metrics-server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace = "kube-system"
}