# open
- mailer
    - https://qiita.com/rubytomato@github/items/b106ff8011bcad60bce2#spring-boot-starter-mail
    - java -jar /Users/ryu.ishikawa/FakeSMTP/target/fakeSMTP-2.1-SNAPSHOT.jar -p 2525 -a 127.0.0.1 -o /tmp/mail/message

# todo
- [x] crawlerの実装
- [ ] word listからranking listに変換する
- [ ] tagの利用
  - https://www.ibm.com/support/knowledgecenter/ja/SS5RWK_3.5.0/com.ibm.discovery.es.ta.doc/iiysspostagset.htm
- [ ] test
- [ ] refactor 
 
## tech
- 非同期
    - ScheduledExecutor
    - Reactor
- batch
    - @Scheduled
- mailer
    - mailer
- uploader
    - [ ] client -> server
    - [ ] server -> s3
    - [x] s3 -> server
    - [ ] server -> client
- [ ] validation
- [ ] transaction
- [ ] messaging
- [ ] Redis
- [ ] DB/MySQL
    - where
    - transaction
    - join
- [ ] DB/MongoDB
- [ ] Json handling
- [ ] 時刻
- [ ] 認証
- [ ] 

https://docs.google.com/spreadsheets/d/1xbnWWRWU3EUZ81c05IQzM_hTbnMFHrCF9iWTG9r-tAM/edit#gid=0


## note
- flywayCleanとdocker-composeの再起動(minio cleanup)をしてから作業始めること