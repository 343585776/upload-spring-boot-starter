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

### 注意事项

因为提供了通用的上传Controller,所以要确保该Controller能够被SpringBoot正确扫描到。

SpringBoot默认扫描方式是从Application启动类所在包向下扫描,如果Application启动类所在层级较深，则需要设置扫描路径。

通用Controller所在包为`me.sdevil507.upload`下面,

### 本地存储配置

启动器提供了`AbstractUploadFileHandler`的子类`LocalUploadFileHandler`实现本地保存上传文件

