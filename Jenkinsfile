pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/klad3/sm-sum-api.git'
        BRANCH = 'main'
        WORKDIR = '/home/deploy/sm-sum-api'
    }

    stages {
        stage('Clone repository') {
            steps {
                script {
                    // Verificamos si el directorio existe, si no, lo clonamos
                    sh """
                        if [ ! -d "${WORKDIR}" ]; then
                            git clone -b ${BRANCH} ${REPO_URL} ${WORKDIR};
                        else
                            cd ${WORKDIR} && git pull origin ${BRANCH};
                        fi
                    """
                }
            }
        }

        stage('Clean up Docker') {
            steps {
                script {
                    // Limpiamos contenedores detenidos, imágenes no usadas, volúmenes huérfanos y redes no utilizadas
                    sh """
                        docker container prune -f
                        docker image prune -a -f
                        docker volume prune -f
                        docker network prune -f
                    """
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Ejecutamos docker-compose para reiniciar el contenedor
                    sh """
                        cd ${WORKDIR} &&
                        docker compose down &&
                        docker compose up -d --build
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }

        success {
            echo 'Deployment successful!'
        }

        failure {
            echo 'Deployment failed!'
        }
    }
}
