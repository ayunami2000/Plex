pipeline {
    agent any
    stages {
        stage("build") {
            steps {
                withGradle {
                    sh "./gradlew build javadoc --no-daemon"
                }
            }
        }
        stage("publish") {
            when {
                branch "master"
            }
            steps {
                withGradle {
                    sh "./gradlew publish --no-daemon"
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: "build/libs/*.jar", fingerprint: true
            javadoc javadocDir: 'build/docs/javadoc', keepAll: false
            discordSend description: "Jenkins", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: env.PLEX_WEBHOOK_URL
            cleanWs()
        }
    }
}