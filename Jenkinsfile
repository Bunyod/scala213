pipeline {
	agent {
		label 'connectivity'
	}
	stages {
			stage('Build image') {
    			steps {
    				sh "docker image build --target build-env -t scala213-dev:${BUILD_NUMBER} ."
    			}
    		}
		stage('Code analysis') {
				steps {
				    sh 'docker run --rm -v sbt-dev:/root scala213-dev:${BUILD_NUMBER} sbt scalastyle scapegoat'
			}
		}
		stage('Test Unit') {
				steps {
				    sh 'docker run --rm -v sbt-dev:/root scala213-dev:${BUILD_NUMBER} sbt coverage test coverageReport'
			}
		}
		stage('Create production image') {
                when {
                    anyOf {
                        branch 'master'; branch 'refactoring'
                    }
                }
				steps {
				    sh "docker image build -t docker.bunic.de:5001/scala213:${BUILD_NUMBER} ."
                    sh "docker push docker.bunic.de:5001/scala213:${BUILD_NUMBER}"
			}
		}
		stage('Deploy Stage') {
		        when {
                    anyOf {
                        branch 'master';
                    }
                }
			steps {
				sh 'echo  \'{ \
                        "container": { \
                          "type": "DOCKER", \
                          "docker": { \
                            "image": "docker.bunic.de:5001/sscala213:\'$BUILD_NUMBER\'", \
                            "network": "BRIDGE", \
                            "portMappings": [ \
                              { \
                                "containerPort": 8080, \
                                "hostPort": 0, \
                                "protocol": "tcp" \
                              } \
                            ] \
                          } \
                        } \
                      }\' | docker run -i k0pernikus/httpie-docker-alpine:1.0.0 PUT http://bunic.de/marathon/v2/apps/seed'
			}
		}
		stage('Deploy Production') {
			input {
				message "Deploy to production?"
					id "simple-input"
			}
			steps {
				echo 'deploy production'
			}
		}
	}
}
