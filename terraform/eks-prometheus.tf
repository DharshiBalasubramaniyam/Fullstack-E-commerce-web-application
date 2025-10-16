resource "helm_release" "prometheus" {
  name       = "prometheus"
  repository = "https://prometheus-community.github.io/helm-charts/"
  chart      = "prometheus"
  namespace = "prometheus"
  create_namespace = true

  set_list = [
    {
      name  = "alertmanager.persistence.storageClass"
      value = ["gp2"]
    },
    {
      name  = "server.persistentVolume.storageClass"
      value = ["gp2"]
    }
  ]
}
