将Authentication模块与GlassfishRealm模块生成的jar文件放到以下目录下
${glassfish-install-root}/domains/domain1/lib/

并且按照src/main/resources/domain1.xml当中关于Realm的配置来更新以下文件auth-realm部分
${glassfish-install-root}/domains/domain1/config/domain.xml