pipeline {
    agent none

    stages {
        stage('Maven Build and Test') {
            agent{
                docker {
                    image 'maven:latest'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps{
                echo 'Maven Test Stage'
                sh 'cd prac && mvn -B clean test'
                echo 'Maven Build Stage'
                sh 'cd prac && mvn package'
            }
	    }
        stage('Image Build'){
            agent{
                label 'master'    
            }
            steps{
                echo 'Image Build Stage'    
                sh "docker build prac/. -t cloud:${BUILD_ID}"
            }
        }
        stage('Image Push'){
            agent{
                label 'master'    
            }
            steps{
                echo 'Image Push Stage'
                sh "docker tag cloud:${BUILD_ID} pkun/cloud:${BUILD_ID}"
                sh "docker login --username=pkun -p li2000chun"
                sh "docker push pkun/cloud:${BUILD_ID}"
            }
        }
    }
}
