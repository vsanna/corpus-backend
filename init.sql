CREATE DATABASE IF NOT EXISTS corpus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER corpus_user IDENTIFIED BY 'corpus_password';
CREATE USER flyway IDENTIFIED BY 'flyway';
GRANT ALL ON corpus.* TO corpus_user;
GRANT ALL ON corpus.* TO flyway;
# 1. run these queries
# 2. あとはflyway任せ

# V1_1の実行後
LOAD DATA LOCAL INFILE '/Users/ryu.ishikawa/rep/corpus/frequent_words.csv'
    INTO TABLE `frequent_words`
    FIELDS TERMINATED BY ','
    (@word, @rank)
    SET word = @word, rank = @rank;
