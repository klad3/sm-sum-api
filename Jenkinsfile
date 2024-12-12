pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/klad3/sm-sum-api.git'
        BRANCH = 'main'
        DEPLOY_SERVER = '157.230.55.190'
        DEPLOY_USER = 'deploy'
        SSH_CREDENTIALS_ID = 'ssh-deploy-credentials'
        WORKDIR = '/home/deploy/sm-sum-api'
        SONARQUBE_SERVER = 'SonarQube'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        DOCKER_COMPOSE_TEST_FILE = 'docker-compose.test.yml'
        SELENIUM_URL = 'http://selenium:4444/wd/hub'
        FRONTEND_BASE_URL = 'http://frontend-container-test:3000/login'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Install Backend Dependencies') {
            steps {
                dir('backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean install -DskipTests'
                }
            }
        }

        stage('Static Code Analysis') {
            steps {
                dir('backend') {
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
                        sh './mvnw sonar:sonar'
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                dir('backend') {
                    sh './mvnw test'
                }
            }
            post {
                always {
                    junit '**/backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Run Functional Tests') {
            steps {
                sh 'curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh'
                sh 'chmod +x wait-for-it.sh'
                sh "docker compose -f ${DOCKER_COMPOSE_TEST_FILE} up -d"
                sh './wait-for-it.sh frontend-container-test:3000 -- echo "Frontend está listo"'
                sh './wait-for-it.sh backend-container-test:8081 -- echo "Backend está listo"'
                sh './wait-for-it.sh selenium:4444 -- echo "Selenium está listo"'
                dir('pruebas_funcionales') {
                    withEnv([ 
                        "SELENIUM_URL=${SELENIUM_URL}",
                        "FRONTEND_BASE_URL=${FRONTEND_BASE_URL}"
                    ]) {
                        sh 'mvn clean test'
                    }
                }
            }
            post {
                always {
                    junit '**/pruebas_funcionales/target/surefire-reports/*.xml'
                    sh "docker compose -f ${DOCKER_COMPOSE_TEST_FILE} down"
                }
            }
        }

        stage('Deploy to VPS') {
            steps {
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} 'cd ${WORKDIR} && git pull origin ${BRANCH}'
                    """
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} 'cd ${WORKDIR} && docker-compose -f ${DOCKER_COMPOSE_FILE} down && docker-compose -f ${DOCKER_COMPOSE_FILE} up -d'
                    """
                }
            }
        }

    }

    post {
        always {
            echo 'Pipeline finalizado.'
        }

        success {
            echo 'Despliegue exitoso.'
        }

        failure {
            echo 'Despliegue fallido.'
        }
    }
}
