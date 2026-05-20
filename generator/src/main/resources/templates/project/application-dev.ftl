spring:
  application:
    name: ${module}

  datasource:
    url: jdbc:mysql://localhost:3306/xxx?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: xxx
    driver-class-name: com.mysql.cj.jdbc.Driver


mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.example.${module}.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

auth:
  jwt:
    secret-key: "这是一串密钥,正常应该长一点，至少几十个字符，不能泄露，不能提交到代码仓库，在-dev配置文件覆盖这个值"
    # 1000 * 60 * 60 * 24 默认 24 小时
    expires-in: 86400000
