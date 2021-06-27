#!/usr/bin/env bash


log(){
    echo "DEMO => ${1}";
}

log "Make sure you have run 'mvn spring-boot:run' in another shell!"

log "Create some users"
createAnotherUser='Y'
while [ "$createAnotherUser" == 'Y' ]
do
    read -p 'name: ' name
    read -p 'accessToken: ' token

    log "Creating user"

    curl -X POST -H "Content-Type: application/json" \
        -d "{\"name\": \"$name\", \"accessToken\": \"$token\"}" \
        localhost:8080/users

    echo
    read -p 'create another? [N/y]: ' createAnotherUser
done

echo
log "Lets have a look at all those users you created"

curl -X GET \
        localhost:8080/users

echo
log "Now it is time to send a notification to one of those users"

sendAnotherMessage='Y'
while [ "$sendAnotherMessage" == 'Y' ]
do
    read -p 'user: ' name
    read -p 'title: ' title
    read -p 'title: ' body

    log "Sending message"

    curl -X POST -H "Content-Type: application/json" \
        -d "{\"title\": \"$title\", \"body\": \"$body\"}" \
        "localhost:8080/push/$name"

    echo
    read -p 'send another? [N/y]: ' sendAnotherMessage
done

log "Thanks for running the demo!"

