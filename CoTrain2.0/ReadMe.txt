Demo展示了3个客户端共同进行协作训练的场景，
需要启动3个客户端程序、3个对应的模型训练程序、3个对应的网页界面
对应的通信端口分别为：
            	Port1 客户端-训练程序    Port2客户端-网页
Client1           	9997                		8887
Client2           	9998                		8888
Client3           	9999                		8889


Step1:
    在终端启动模型训练程序/PythonServer/main.py
    1. cd ./PythonServer
    2. python main.py port1

Step2:
    运行客户端程序/src/main.java
    1. 在代码中修改对应的端口号
    2. 运行程序

Step3:
    进入Init界面设置相关信息
    1. 打开 localhost:port2/home
    2. Init界面填写信息如下并单击设置
	access id: "LABOycAOKiS2088132559930274"
	accessSecretPath: ./ca/Baas/access.key 绝对路径
	
	User1:
		accountName = "user1ofIFBDAChain"
		accountKeyPath = ./ca/user1/key/user.key 绝对路径
		accountPassword = "123456cyb"
	User3:
		accountName = "user3ofIFBDAChain"
		accountKeyPath = ./ca/user1/key/user3.key 绝对路径
		accountPassword = "123456cyb"
	User4:
		accountName = "user4ofIFBDAChain"
		accountKeyPath = ./ca/user1/key/user4.key 绝对路径
		accountPassword = "123456cyb"

	contract name: "cotrain101703"
	contract path:  ./contract/CoTrain/target/co_train.wasc绝对路径

    3. 在三个客户端的其中一个界面单击 "Init collaborators"

Steps:
    进入CoTrain界面开始并监视协作训练
    1. 单击左侧导航栏的CoTrain
    2. 单击左上角“Start Training”
    3. 界面上方显示了客户端当前状态
    4. 折线图显示了全局模型测试精度的变化曲线




