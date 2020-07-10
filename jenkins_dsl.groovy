job('github-pull-t6'){
     scm{
             github('Radhika299/task-6-jenkins-dsl')      
         }       

    triggers{
                upstream('seed-job-t6','SUCCESS')
           }
    
    steps{

	shell('if ls | grep .html; then sudo docker build -t radhu299/httpd-task6 . -f html.Dockerfile; sudo docker push radhu299/httpd-task6;  elif ls | grep .php; then sudo docker build -t radhu299/php-task6 . -f php.Dockerfile; sudo docker push  radhu299/php-task6;  else echo "other files are present not needed to build & push image.."; fi')
       
       }
    
}


job('auto-deploy-t6'){
     triggers{
            upstream('github-pull-t6','SUCCESS')
         }
     steps{
	shell('cd /var/lib/jenkins/workspace/seed-job-t6/; if ls  | grep .html; then if sudo kubectl get deployment | grep web-html-deployment; then echo "deployment for html application already running"; kubectl rollout restart deployment/web-html-deployment; else sudo kubectl create -f /task6-jenkinsdsl/htmlpv.yml; sudo kubectl create -f /task6-jenkinsdsl/htmldeployment.yml;  sudo kubectl create -f /task6-jenkinsdsl/htmlexpose.yml; fi;  elif ls | grep .php; then if sudo kubectl get deployment | grep web-php-deployment; then echo "deployment for php application already running"; kubectl rollout restart deployment/web-php-deployment; else sudo kubectl create -f /task6-jenkinsdsl/phppv.yml; sudo kubectl create -f /task6-jenkinsdsl/phpdeployment.yml;  sudo kubectl create -f /task6-jenkinsdsl/phpexpose.yml; fi;  else echo "other files can not deploy automatically"; fi')
        }

}

job('testing and email-sending-t6'){
     triggers{
           upstream('auto-deploy-t6','SUCCESS')
         }
    steps{
            shell('if sudo kubectl get svc html-pod-expose ; then status1=$( curl -o /dev/null  -s -w "%{http_code}" http://192.168.99.104:31000/index.html ); if [[ $status1 == 200  ]]; then exit 0; else exit 1; fi; elif sudo kubectl get svc php-pod-expose; then status1=$( curl -o /dev/null  -s -w "%{http_code}" http://192.168.99.104:32000/index.php ); if [[ $status1 == 200  ]]; then exit 0; else exit 1; fi;  else echo "No pod is exposed , so no need of testing"; fi')
       }
     publishers {
        extendedEmail {
            recipientList('radhey2900@gmail.com')
    
            triggers {
     
                failure {
                    
                    sendTo {
                        recipientList()
                    }
                }
            }
        }
    }

}


buildPipelineView('task-6-build-pipelineview') {
    title('task-6-build-pipelineview')
    displayedBuilds(3)
    selectedJob('seed-job-t6')
}




































