pipeline {
    agent any

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
            steps{
                echo 'Image Build Stage'
                sh 'docker build prac/. -t cloud:latest'
            }
        }
        stage('Image Push'){
            steps{
                echo 'Image Push Stage'
                sh "docker tag cloud:latest harbor.edu.cn/cn202004/cloud:latest"
                sh 'cat ~/.docker/config.json | base64 -w 0'
                sh "docker login --username=cn202004 harbor.edu.cn -p cn202004 && docker push harbor.edu.cn/cn202004/cloud:latest"
            }
        }
    }
}

node('slave') {
    container('jnlp-kubectl') {
        stage('connect'){
            sh 'curl "http://p.nju.edu.cn/portal_io/login" --data "username=181250068&password=li2000chun"'
        }
        stage('git clone'){
            git url: "https://github.com/pppppkun/cloudNativePractice.git"
        }
        
        stage('Yaml'){
            echo "Yaml File Stage"
            sh 'ls -a -l'
        }

        stage('Deploy'){
            echo "Deploy To k8s Stage"
            sh 'kubectl apply -f secret.yaml -n cn202004'
            sh 'kubectl apply -f cloud.yaml -n cn202004'
        }

    }
}