resource "aws_iam_openid_connect_provider" "alb_openid_connect_provider" {
  url = data.aws_eks_cluster.purely_cluster.identity[0].oidc[0].issuer

  client_id_list = [
    "sts.amazonaws.com",
  ]

  thumbprint_list = ["9e99a48a9960b14926bb7f3b02e22da2b0ab7280"]
}
