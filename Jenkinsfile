def label = "slave-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
   containerTemplate(name: 'docker', image: 'docker', command: 'cat', privileged: true, ttyEnabled: true),
   containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl', ttyEnabled: true, command: 'cat')
], volumes: [
   hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
]){
 node(label) {
    checkout scm

    stage('Test') {
      try {
        sh """
          ./gradlew test
          """
      }
      catch (exc) {
        println "Failed to test - ${currentBuild.fullDisplayName}"
        throw(exc)
      }
    }

    stage('Build') {
      sh "./gradlew build"
    }

    stage('Create Docker images') {
      container('docker') {
        def credential = "ecr:${AWS_REGION}:${ECR_CREDENTIAL}"
        docker.withRegistry("https://${ECR_HOST}", credential) {
              sh """
                docker build -t ${ECR_HOST}/sample-service:${currentBuild.number} -t ${ECR_HOST}/sample-service:latest .
                docker push ${ECR_HOST}/sample-service:${currentBuild.number}
                docker push ${ECR_HOST}/sample-service:latest
                """
         }
       }
    }

    stage('Deploy') {
      container('kubectl') {
        sh """
           kubectl set image --namespace=poc deployments/sample-service-deployment sample-service=${ECR_HOST}/sample-service:${currentBuild.number}
           kubectl --namespace=poc rollout status deployments/sample-service-deployment
        """
      }
    }
  }
}
