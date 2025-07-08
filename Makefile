REGISTRY=swr.cn-north-4.myhuaweicloud.com
PRODORG=prod-bcp-x86
TESTORG=prod-bcp-x86
ARMORG=prod-bcp-arm
NAME=bcp-agent
NAMESIT=bcp-agent
TAG=$(shell date +%Y%m%d%H%M)
PRODTAG=1-0-0-x86

base:
    mvn clean package

prod: base
    echo building "正在服务器打包${NAME}生产镜像..."
	echo building ${NAME}:master
	cp src/main/docker/Dockerfile .
	docker build -t ${REGISTRY}/${PRODORG}/${NAME}:${PRODTAG} .
	rm Dockerfile
	docker push ${REGISTRY}/${PRODORG}/${NAME}:${PRODTAG}

sit: base
	echo building "正在服务器打包${NAMESIT}测试镜像..."
	echo building ${NAMESIT}:${TAG}
	cp src/main/docker/Dockerfile .
	docker build -t ${REGISTRY}/${TESTORG}/${NAMESIT}:${TAG} .
	rm Dockerfile
	docker push ${REGISTRY}/${TESTORG}/${NAMESIT}:${TAG}

arm: base
	echo building "正在服务器打包${NAMESIT}镜像，arm环境..."
	echo building ${NAMESIT}:${TAG}
	cp src/main/docker/Dockerfile .
	docker buildx build --platform linux/arm64 -t ${REGISTRY}/${ARMORG}/${NAMESIT}:${TAG} . --push
	rm Dockerfile

local: base
	echo building "正在本地打包${NAME}镜像..."
	cp src/main/docker/Dockerfile .
	docker build -t ${NAME}:beta .
	rm Dockerfile