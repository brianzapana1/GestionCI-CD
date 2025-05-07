pipeline {
    agent any

    tools {
        jdk 'JDK 21'          
        maven 'Maven 3.9.9'
    }

    stages {
        stage('Clonar Repositorio') {
            steps {
                git 'https://github.com/brianzapana1/GestionCI-CD.git'
            }
        }

        stage('Compilación') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Pruebas') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Empaquetado') {
            steps {
                bat 'mvn package'
            }
        }

        stage('Despliegue Local') {
            steps {
                echo '🎯 Ejecutando la aplicación local...'
                bat 'java -jar target/Gestion-inventario-1.0-SNAPSHOT.jar'
            }
        }
    }

    post {
        success {
            echo '✅ Build completado exitosamente'
        }
        failure {
            echo '❌ Hubo un error durante el pipeline'
        }
    }
}
