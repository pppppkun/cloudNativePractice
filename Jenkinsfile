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
                sh 'docker build prac/. -t cloud:latest'
            }
        }
        stage('Image Push'){
            steps{
                echo 'Image Push Stage'
                sh "docker tag cloud:latest harbor.edu.cn/cn202004/cloud:latest"
                sh "docker login --username=cn202004 harbor.edu.cn -p cn202004 && docker push harbor.edu.cn/cn202004/cloud:latest"
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