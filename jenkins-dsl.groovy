job('auto-deploy-t6'){
     triggers{
            upstream('seed-job-githubpull-t6','SUCCESS')
         }
     steps{
	shell('echo its completed')
        }

}

job('testing and email-sending-t6'){
     triggers{
           upstream('auto-deploy-t6','SUCCESS')
         }
    steps{
            shell('exit 1')
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
