echo
echo "Keystore generator (destalias: tomcat) - using certbot, openssl and keytool"
echo "IMPORTANT: It may be necessary to stop running (web-)servers binded to port 80 due to certbot"
echo
echo "Enter cluster hostname (eg. cluster.yourdomain.com):"
read clHost
echo "Enter keystore pass (recommended at least 16 characters):"
read clPass
echo "Enter keystore directory (eg. /home/roomtube/ssl - without / at the end):"
read clDir

echo 
echo "Keystore generating..."
echo

certbot certonly --standalone -d $clHost
openssl pkcs12 -export -in /etc/letsencrypt/live/$clHost/cert.pem -inkey /etc/letsencrypt/live/$clHost/privkey.pem -out $clDir/$clHost.p12 -name $clHost -CAfile /etc/letsencrypt/live/$clHost/fullchain.pen -caname "Let's Encrypt Authority X3" -password pass:$clPass
keytool -importkeystore -deststorepass $clPass -destkeypass $clPass -deststoretype pkcs12 -srckeystore $clDir/$clHost.p12 -srcstoretype PKCS12 -srcstorepass $clPass -destkeystore $clDir/$clHost.keystore -alias $clHost -destalias tomcat

rm -rf $clDir/$clHost.p12

echo
echo "Keystore generated: "$clDir"/"$clHost".keystore"
echo
echo "Change keystore in Cluster config.json to: "$clDir"/"$clHost".keystore"
echo "Change keystore.pass in config.json in Cluster config.json to: "$clPass
echo