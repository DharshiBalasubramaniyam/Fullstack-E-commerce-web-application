provider "aws" {
	region = var.region
}

provider "kubernetes" {
  host = data.aws_eks_cluster.purely_cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.purely_cluster.certificate_authority[0].data)
  token = data.aws_eks_cluster_auth.purely_cluster.token
}

provider "helm" {
  kubernetes = {
    host = data.aws_eks_cluster.purely_cluster.endpoint
  	cluster_ca_certificate = base64decode(data.aws_eks_cluster.purely_cluster.certificate_authority[0].data)
  	token = data.aws_eks_cluster_auth.purely_cluster.token
  }
}