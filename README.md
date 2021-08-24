![Continuous Delivery](https://github.com/hyrepo/apartment-registration-alert/actions/workflows/cicd.yml/badge.svg) ![Coverage](.github/badges/jacoco.svg) ![License](https://img.shields.io/badge/License-MIT-green)

This application crawls apartments information hourly, if there are new apartments in particular areas open for
purchase, then an email notification will be triggered.

The logic is pretty simple, but the main purpose of this application is to try out:

- Running an application in the cloud for totally free
- Serverless computation
- Cloud-native

# Architecture

![Architecture](https://github.com/hyrepo/apartment-registration-alert/blob/master/doc/architecture.png)

# Cost

All cloud components are covered in the free tier as bellow, this application cost $0.

|Component|Type|Free Tier|Platform|
|:---:|:---:|:---:|:---:|
|Cloud Functions|Serverless Platform(FaaS)|200M calls / month| GCP
|Cloud Scheduler|Scheduler|3 jobs / month| GCP
|Cloud Pub/Sub|Message Service|10GB / month| GCP
|Cloud Firestore|NoSQL DB|1GB storage, 50000 reads & 20000 writes / day| GCP
|SNS|Notification Service|1000 Email| AWS