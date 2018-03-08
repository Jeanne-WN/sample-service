def label = "slave-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
   containerTemplate(name: 'docker', image: 'docker', command: 'cat', privileged: true, ttyEnabled: true)
], volumes: [
   hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
]){
 node(label) {
    checkout scm

    stage('Test') {
      try {
        sh """
          pwd
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
        withCredentials([[$class: 'UsernamePasswordMultiBinding',
          credentialsId: 'ecr',
          usernameVariable: 'ECR_USER',
          passwordVariable: 'ECR_PASSWORD']]) {
              sh """
                docker login -u ${ECR_USER} -p ${ECR_PASSWORD} https://${ECR_HOST}
                docker build -t ${ECR_HOST}/sample-service:${currentBuild.number} .
                docker push ${ECR_HOST}/sample-service:${currentBuild.number}
                """
         }
       }
    }
  }
}