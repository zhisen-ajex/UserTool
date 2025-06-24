# Build
mvn clean compile install -DskipTests && \

# Docker  Build
docker image build -t verify-gke-sql:0.0.2 . && \
# Push to local registry
#docker image tag verify-gke-sql0.0.2 zhisen2/verify-gke-sql0.0.2
#docker image push zhisen2/verify-gke-sql0.0.2
# Push to stage registry
docker image tag verify-gke-sql:0.0.2 asia-south2-docker.pkg.dev/middleware-test-339406/images/ajex/verify-gke-sql:0.0.2 && \
docker image push asia-south2-docker.pkg.dev/middleware-test-339406/images/ajex/verify-gke-sql:0.0.2

# Push to production registry
#docker image tag verify-gke-sql0.0.2 asia-south1-docker.pkg.dev/ajex-production/images/ajex/verify-gke-sql0.0.2 && \
#docker image push asia-south1-docker.pkg.dev/ajex-production/images/ajex/verify-gke-sql0.0.2