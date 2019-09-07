## 通用单文件/多文件上传场景启动器

1. 该启动器支持单文件与多文件的上传操作
2. 支持文件存储本地(默认)
3. 支持拓展使用云存储

### 引入starter

```xml
<dependency>
    <groupId>me.sdevil507</groupId>
    <artifactId>upload-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 本地存储配置

启动器提供了`AbstractUploadFileHandler`的子类`LocalUploadFileHandler`实现本地保存上传文件

