import socket
import torch
import torch.nn as nn
import time
import sys
import numpy as np
from torch.utils.data import DataLoader, TensorDataset
from torchvision import datasets
from flask import Flask, render_template, request, jsonify


# 神经网络模型
class NeuralNetwork(nn.Module):
    def __init__(self, n_input, n_output):
        super(NeuralNetwork, self).__init__()
        self.hidden = nn.Linear(n_input, 128)
        self.predict = nn.Linear(128, n_output)

    def forward(self, input):
        out = self.hidden(input)
        out = torch.relu(out)
        out = self.predict(out)

        return out


def train_loop(dataloader, model, loss_fn, optimizer):

    # 训练数据规模
    size = len(dataloader.dataset)

    time_last = time.time()

    # 遍历dataloader中的每一个batch
    for batch, (X, y) in enumerate(dataloader):
        # 计算的预测值与误差
        pred = model(X)
        loss = loss_fn(pred, y)

        # 反向传播，计算梯度
        optimizer.zero_grad()
        loss.backward()

        # 更新模型参数
        optimizer.step()

        # 每100个batch输出一次
        if batch % 100 == 0:
            t_now = time.time()
            interval = t_now - time_last
            time_last = t_now
            loss, current = loss.item(), batch * len(X)
            print(f"loss: {loss:>7f} [{current:>5d}/{size:>5d}] cost:{interval}s")


def test_loop(dataloader, model, loss_fn):
    # 测试数据规模
    size = len(dataloader.dataset)

    # batch数量
    num_bataches = len(dataloader)

    # loss, 准确率
    testloss, correct = 0, 0

    # 不需要计算梯度
    with torch.no_grad():
        for X, y in dataloader:
            pred = model(X)
            testloss += loss_fn(pred, y).item()
            correct += (pred.argmax(1) == y).type(torch.float).sum().item()

        testloss /= num_bataches
        correct /= size
        print(f"Test Error: \n Accuracy: {(100 * correct):>0.1f}%, Avg loss:{testloss:>8f}\n")

    return testloss, correct


def setup_seed(seed):
    torch.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)
    np.random.seed(seed)
    torch.backends.cudnn.deterministic = True


if __name__ == '__main__':
    # 端口设置
    port = sys.argv[1]

    # 固定随机数种子
    setup_seed(47)

    # 训练超参
    learning_rate = 1e-3  # 学习率
    batch_size = 64  # 每个batch有64条数据
    epochs = 30  # 训练10轮
    mergeInterval = 5  # 聚合间隔

    # 数据
    data_train = np.load('data_train_' + port + '.npy')  # 共600个数据(22个类别：1类正常，21类故障)
    data_test = np.load('data_test.npy')  # 共600个数据
    label_train = np.load('label_train_' + port + '.npy').squeeze(1)
    label_test = np.load('label_test.npy').squeeze(1)

    # 转成torch格式
    traindata = torch.from_numpy(data_train).float()
    trainlabel = torch.from_numpy(label_train).float().type(torch.LongTensor)
    data_test = torch.from_numpy(data_test).float()
    label_test = torch.from_numpy(label_test).float().type(torch.LongTensor)

    # 转dataset
    training_data = TensorDataset(traindata, trainlabel)
    test_data = TensorDataset(data_test, label_test)

    # dataloader
    train_dataloader = DataLoader(training_data, batch_size=batch_size, shuffle=True)
    test_dataloader = DataLoader(test_data, batch_size=batch_size, shuffle=True)

    # 建立神经网络模型结构
    model = NeuralNetwork(52, 22)
    # 定义损失函数
    loss_fn = nn.CrossEntropyLoss()
    # 优化器 Adam
    optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate)

    # http post
    app = Flask(__name__)


    @app.route("/getLocalModel", methods=["GET", "POST"])
    def getLocalModel():
        # 首先获取前端传入的name数据
        if request.method == "POST":
            address = request.form.get("address")
        if request.method == "GET":
            address = request.args.get("address")

        print(address)

        # 读取本地模型参数，转list
        flattened_pam = torch.tensor([])
        for _, param in enumerate(model.parameters()):
            flattened_pam = torch.cat([flattened_pam, torch.flatten(param.detach())])
        pam_array = flattened_pam.numpy()
        np.savetxt(address, pam_array, delimiter=' ', fmt='%.8f')

        return "服务器将训练后的模型参数保存在本地目录"+address


    @app.route("/sendGlobalModel", methods=["GET", "POST"])
    def sendGlobalModel():
        # 首先获取前端传入的name数据
        if request.method == "POST":
            address = request.form.get("address")
        if request.method == "GET":
            address = request.args.get("address")

        pam_array = np.loadtxt(address, delimiter=' ')
        avg_pam = torch.from_numpy(pam_array)
        state_dict = model.state_dict()
        for param_tensor in state_dict:
            shape = state_dict[param_tensor].size()
            num = state_dict[param_tensor].numel()
            pam_flatten, avg_pam = avg_pam.split([num, avg_pam.numel() - num], dim=0)
            state_dict[param_tensor] = pam_flatten.reshape(shape)
        model.load_state_dict(state_dict)

        return "服务器完成模型参数的更新"


    @app.route("/beginTrainModel", methods=["GET", "POST"])
    def beginTrainModel():
        # 遍历数据mergeInterval遍
        for t in range(mergeInterval):
            print(f"\nEpoch {t + 1}\n-------------------------")
            train_loop(train_dataloader, model, loss_fn, optimizer)

        return "服务器完成本地训练"


    @app.route("/getTestResult", methods=["GET", "POST"])
    def getTestResult():
        print("local model test:")
        loss, acc = test_loop(test_dataloader, model, loss_fn)

        return str(acc*100)

    app.run(port=int(port))





