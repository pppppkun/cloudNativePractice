def tag = '1.0.5'

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
                sh "docker image rm -f 254ad0cdaa12"
                
                sh "docker build prac/. -t cloud:${tag}"
            }
        }
        stage('Image Push'){
            agent{
                label 'master'    
            }
            steps{
                echo 'Image Push Stage'
                sh "docker tag cloud:${tag} harbor.edu.cn/cn202004/cloud:${tag}"
                sh 'cat ~/.docker/config.json | base64 -w 0'
                sh "docker login --username=cn202004 harbor.edu.cn -p cn202004 && docker push harbor.edu.cn/cn202004/cloud:${tag}"
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
	    sh 'sed -i "s#0.0.0#1.0.5#g" cloud.yaml' 
	}
        stage('Delete'){
            echo "Delete old deploment and svc"
            sh 'kubectl delete deployment cloud -n cn202004'
            sh 'kubectl delete svc cloud -n cn202004'
        }
        stage('Deploy'){
            echo "Deploy To k8s Stage"
            sh 'kubectl apply -f secret.yaml -n cn202004'
            sh 'kubectl apply -f cloud.yaml -n cn202004'
            sh 'kubectl apply -f cloud-serviceMonitor.yaml -n cn202004'
        }

    }
}
