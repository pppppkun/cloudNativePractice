pipeline {
    agent any

    stages {
        stage('Maven Build') {
		steps{
			sh 'curl "http://p.nju.edu.cn/portal_io/login" --data "username=181250068&password=li2000chun"'
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
