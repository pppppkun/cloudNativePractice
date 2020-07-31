pipeline {
    agent any

    stages {
        stage('Maven Build') {
            agent{
                docker {
                    image 'maven:latest'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps{
                echo 'Maven Build Stage'
                sh 'cd prac && mvn -B clean package -Dmaven.test.skip=true'
            }
	    }
        stage('Image Build'){
            steps{
                echo 'Image Build Stage'
                sh 'docker build prac/. -t pkun/cloud:latest'
            }
        }
        stage('Image Push'){
            steps{
                echo 'Image Push Stage'
                sh "docker login --username=pkun -p li2000chun && docker push pkun/cloud:latest"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}