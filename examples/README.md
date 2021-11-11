# Geoip-API deployment examples

This folder contains examples, how to deploy Geoip-API in different scenarios.

## `docker-compose.yaml`

A very simple [Docker-Compose example](docker-compose.yaml).

## `kubernetes-deployment.yaml`

A [simple Kubernetes deployment](kubernetes-deployment.yaml) that creates a deployment and a service.
Use the Ingress definition below to direct web traffic to your Geoip-API
instance.

```yaml
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: geoip
spec:
  rules:
  - host: geoip.YOURDOMAIN
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: geoip
            port:
              name: web
```

## [nginx-geoip](nginx-geoip/)

A more comprehensive example to supply geoip information to Nginx using a centralized
Geoip-API webservice.
