minikube addons enable ingress
kubectl create configmap keycloak-config --from-file keycloak/config/all.json
kubectl create secret generic pgpassword --from-literal PGPASSWORD=password 
kubectl apply -f k8s