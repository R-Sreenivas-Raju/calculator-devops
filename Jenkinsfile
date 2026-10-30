// ─────────────────────────────────────────────────────────────────────────────
// SPE Mini-Project: Calculator CI/CD Pipeline
//
// This is a DECLARATIVE Jenkins pipeline.
// It automates: Build → Test → Docker Build → Push to Docker Hub → Deploy
//
// Trigger: GitHub Webhook (push to main branch)
// Notifications: Email on success/failure
// ─────────────────────────────────────────────────────────────────────────────

pipeline {

    // Run on any available Jenkins agent
    agent any

    // ── Environment Variables ─────────────────────────────────────────────────
    environment {
        // Docker Hub image name — configured with your Docker Hub username
        DOCKER_IMAGE      = "sreenivas20067/calculator-api"
        DOCKER_TAG        = "${BUILD_NUMBER}"   // e.g. "42" — unique per build
        DOCKER_FULL_IMAGE = "${DOCKER_IMAGE}:${DOCKER_TAG}"
        DOCKER_LATEST     = "${DOCKER_IMAGE}:latest"

        // Jenkins Credential ID for Docker Hub (set this up in Jenkins → Credentials)
        // NEVER hard-code passwords here — always use Jenkins Credentials Store
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials"

        // Student email for build notifications
        STUDENT_EMAIL = "Sreenivasa.Raju@iiitb.ac.in"
    }

    // ── Build Options ─────────────────────────────────────────────────────────
    options {
        // Keep only the last 10 builds to save disk space
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Fail the build if it runs longer than 30 minutes
        timeout(time: 30, unit: 'MINUTES')
        // Add timestamps to every log line
        timestamps()
        // Don't run the same pipeline concurrently (prevents deploy conflicts)
        disableConcurrentBuilds()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STAGES
    // Each stage maps to one column in the Jenkins Blue Ocean view
    // ─────────────────────────────────────────────────────────────────────────
    stages {

        // ── STAGE 1: Checkout ──────────────────────────────────────────────────
        // Jenkins pulls the latest code from GitHub.
        // The GitHub webhook triggers this pipeline on every push to main.
        stage('Checkout') {
            steps {
                echo '=== STAGE 1: Checkout — Pulling latest code from GitHub ==='
                // 'checkout scm' uses the repository configured in the Jenkins job
                checkout scm
                echo "Branch: ${env.GIT_BRANCH}"
                echo "Commit: ${env.GIT_COMMIT}"
                sh 'ls -la'
            }
        }

        // ── STAGE 2: Build ────────────────────────────────────────────────────
        // Compiles the Java code and packages it into an executable JAR.
        // -DskipTests: we skip tests here because Stage 3 runs them separately
        //              (this is the standard CI pattern — build fast, then test)
        stage('Build') {
            steps {
                echo '=== STAGE 2: Build — Compiling and packaging with Maven ==='
                sh 'mvn clean package -DskipTests -B'
                echo 'Build successful! JAR created in target/'
                sh 'ls -lh target/*.jar'
            }
        }

        // ── STAGE 3: Test ─────────────────────────────────────────────────────
        // Runs ALL JUnit 5 tests (unit + MockMvc integration tests).
        // If ANY test fails → the entire build is marked FAILED.
        // Jenkins publishes the JUnit XML report for the test trend graph.
        stage('Test') {
            steps {
                echo '=== STAGE 3: Test — Running JUnit 5 and MockMvc tests ==='
                sh 'mvn test -B'
            }
            post {
                always {
                    // Publish JUnit results to Jenkins — shows pass/fail per test
                    // The XML files are in target/surefire-reports/
                    junit 'target/surefire-reports/*.xml'
                    echo 'Test results published to Jenkins.'
                }
                failure {
                    echo 'TEST FAILED — Build will be marked as FAILED'
                }
            }
        }

        // ── STAGE 4: Docker Build ─────────────────────────────────────────────
        // Builds the Docker image using our multi-stage Dockerfile.
        // Tags the image with the Jenkins build number for traceability.
        // e.g.: yourusername/calculator-api:42
        stage('Docker Build') {
            steps {
                echo "=== STAGE 4: Docker Build — Building image ${DOCKER_FULL_IMAGE} ==="
                sh """
                    docker build \
                        --tag ${DOCKER_FULL_IMAGE} \
                        --tag ${DOCKER_LATEST} \
                        --build-arg BUILD_NUMBER=${BUILD_NUMBER} \
                        .
                """
                echo "Docker image built: ${DOCKER_FULL_IMAGE}"
                sh 'docker images | grep calculator'
            }
        }

        // ── STAGE 5: Push to Docker Hub ───────────────────────────────────────
        // Pushes the image to Docker Hub using credentials stored in Jenkins.
        // The 'withCredentials' block safely injects username/password
        // as environment variables — they are NEVER logged or stored in code.
        stage('Push') {
            steps {
                echo "=== STAGE 5: Push — Pushing ${DOCKER_FULL_IMAGE} to Docker Hub ==="
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CREDENTIALS_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_FULL_IMAGE}
                        docker push ${DOCKER_LATEST}
                        docker logout
                    """
                }
                echo "Image pushed successfully to Docker Hub!"
            }
        }

        // ── STAGE 6: Deploy ───────────────────────────────────────────────────
        // Deploys the full stack using Docker Compose.
        // 'docker compose up -d --build' is idempotent:
        //   - If containers are already running, it updates them gracefully
        //   - The '--build' flag ensures the image is rebuilt
        //   - '-d' runs containers in detached (background) mode
        stage('Deploy') {
            steps {
                echo '=== STAGE 6: Deploy — Deploying with Docker Compose ==='
                sh """
                    # Export the image tag so docker-compose.yml uses the correct version
                    export DOCKER_IMAGE=${DOCKER_FULL_IMAGE}

                    # Pull the latest image (just pushed to Docker Hub)
                    docker pull ${DOCKER_FULL_IMAGE}

                    # Deploy/update the stack — idempotent, safe to re-run
                    docker compose up -d --remove-orphans

                    # Wait for the app to start (Spring Boot takes ~20-30 seconds)
                    echo 'Waiting for application to start...'
                    sleep 30
                """

                // Verify deployment by hitting the health endpoint
                sh """
                    echo 'Verifying deployment health...'
                    response=\$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
                    if [ "\$response" = "200" ]; then
                        echo "Health check PASSED (HTTP \$response)"
                    else
                        echo "Health check FAILED (HTTP \$response)"
                        exit 1
                    fi
                """

                // Show running containers
                sh 'docker compose ps'
                echo 'Deployment successful!'
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST — Actions that run after ALL stages complete
    // ─────────────────────────────────────────────────────────────────────────
    post {

        // Runs ALWAYS (success, failure, or unstable)
        always {
            echo 'Pipeline completed. Cleaning up Docker login session.'
            sh 'docker logout || true'
        }

        // Runs only on SUCCESS
        success {
            echo "BUILD SUCCESS — Image: ${DOCKER_FULL_IMAGE}"
            echo "Notification email sent to ${STUDENT_EMAIL}"
            // Optional email notification step (fails gracefully if SMTP not set up)
            catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                emailext(
                    subject: "[SUCCESS] SPE Calculator Pipeline #${BUILD_NUMBER}",
                    body: "Build Successful! Docker Image: ${DOCKER_FULL_IMAGE}",
                    to: "${STUDENT_EMAIL}"
                )
            }
        }

        // Runs only on FAILURE
        failure {
            echo 'BUILD FAILED!'
            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                emailext(
                    subject: "[FAILED] SPE Calculator Pipeline #${BUILD_NUMBER}",
                    body: "Build Failed! Check logs at ${env.BUILD_URL}console",
                    to: "${STUDENT_EMAIL}"
                )
            }
        }

        // Runs when tests pass but there are warnings (unstable)
        unstable {
            echo 'Build is UNSTABLE — some tests may have failed.'
            emailext(
                subject: "[UNSTABLE] SPE Calculator Pipeline #${BUILD_NUMBER}",
                body: "Build #${BUILD_NUMBER} is unstable. Check test results at ${env.BUILD_URL}",
                to: "${STUDENT_EMAIL}"
            )
        }
    }
}
