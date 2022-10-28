#include <mychainlib/contract.h>
#include <string>
#include <vector>
#include "generated/co_train_ant_generated.h"

#define MODEL_SIZE 100

CONTRACT_VERSION(1);

using namespace mychain;
using namespace cotrain;
class CoTrainContract : public Contract {
public:
    // 存储的根指针
    CoTrainMPtr m_pCoTrain;
    std::vector<std::string> Ids = {"d0f9f8f4736e69e1ee6fd34b0216b4ba569e30c4b7f108c54f50f1eabe9df64c",     //user1
                                    "71bdbbdffa942f50401f4cfe692f027ebc59337673583d734adf61c0cbdfd442",     //user2
                                    "52fcb3b77f1100ecfb8d58ce62c4d4549c50becd8d04e735e909ebef85d769d7"};    //user3
    CoTrainContract() {
      m_pCoTrain = GetCoTrainM();
    }

    INTERFACE void Init() {
        // 这里插入合约初始化存储结构的代码
        InitRoot();
    }

    // 添加客户端名单
    INTERFACE void addClient(){
        for(unsigned long i=0; i<3; i++){
            auto p = m_pCoTrain->get_clients()->add_element(Ids[i]);
            p->set_upload(false);
            p->set_download(false);
            p->set_localmodel("");
        }
        m_pCoTrain->set_status("upload");
    }

    // 本地模型上传
    INTERFACE void upload(std::string str){
        // 合法性校验
        std::string id = GetOrigin().to_hex();
        Require(m_pCoTrain->get_clients()->has_element(id), "Account illegal.");     // 在客户端名单内
        Require(m_pCoTrain->get_status() == "upload", "Status illegal.");            // 在上传阶段
        Require(m_pCoTrain->get_clients()->get_element(id)->get_upload() == false, "had updated already."); // 本轮还未上传

        // 存入数据
        auto p = m_pCoTrain->get_clients()->get_element(id);
        p->set_localmodel(str);

        // 修改状态
        p->set_upload(true);

        // 判断是否聚合
        bool allUpload = true;
        for(unsigned long i=0; i<3; i++){
            if(m_pCoTrain->get_clients()->get_element(Ids[i])->get_upload() == false){
                allUpload = false;
                break;
            }
        }
        // 所有客户端均已经上传
        if(allUpload){
            // 模型聚合
            modelmerge();
            // 修改合约状态为下载
            m_pCoTrain->set_status("download");
            for(unsigned long i=0; i<3; i++){
                m_pCoTrain->get_clients()->get_element(Ids[i])->set_download(false);
            }
        }
    }

    // 模型聚合
    void modelmerge(){
        // 读取各客户端上传的本地模型(string), 转化为double类型
        std::vector<std::vector<double>> localmodels;
        for(unsigned long i=0; i<3; i++){
            std::string localmodel_str = m_pCoTrain->get_clients()->get_element(Ids[i])->get_localmodel();
            localmodels.push_back(StringToDoubleVector(localmodel_str, ' '));
        }
        // 对模型求取平均值得到global model
        std::vector<double> globalmodel;
        double sum = 0;
        for(unsigned long i=0; i<localmodels[0].size(); i++){
            sum = 0;
            for(unsigned long j=0; j<3; j++){
                sum += localmodels[j][i];
            }
            globalmodel.push_back(sum / 3.0);
        }
        // 将global model 转化为string并写入
        m_pCoTrain->get_globalmodel()->set_model(DoubleVectorToString(globalmodel));
    }

    // 全局模型下载
    INTERFACE std::string download(){
        // 合法性校验
        std::string id = GetOrigin().to_hex();
        Require(m_pCoTrain->get_clients()->has_element(id), "Account illegal");
        Require(m_pCoTrain->get_status() == "download", "Status illegal");
        Require(m_pCoTrain->get_clients()->get_element(id)->get_download() == false, "had downdated already."); // 本轮还未上传

        // 修改状态
        auto p = m_pCoTrain->get_clients()->get_element(id);
        p->set_download(true);

        // 判断是否全部完成下载
        bool allDownload = true;
        for(unsigned long i=0; i<3; i++){
            if(m_pCoTrain->get_clients()->get_element(Ids[i])->get_download() == false){
                allDownload = false;
                break;
            }
        }
        // 所有客户端均已经下载
        if(allDownload){
            // 修改合约状态为上传
            m_pCoTrain->set_status("upload");
            for(unsigned long i=0; i<3; i++){
                m_pCoTrain->get_clients()->get_element(Ids[i])->set_upload(false);
            }
        }
        return m_pCoTrain->get_globalmodel()->get_model();
    }

    // 读取状态，测试用
    INTERFACE std::string getStatus(){
        std::string res;
        res = res + "status: " + m_pCoTrain->get_status() + "  \n";
        for(unsigned long i=0; i<3; i++){
            res = res + std::to_string(m_pCoTrain->get_clients()->get_element(Ids[i])->get_upload()) + "  ";
            res = res + std::to_string(m_pCoTrain->get_clients()->get_element(Ids[i])->get_download()) + "  ";
            res = res + "  \n";
        }
        return res;
    }

    // double容器转为字符串，分隔符为' '
    std::string DoubleVectorToString(std::vector<double>& data)
    {
        std::string res;
        unsigned long i;
        for(i=0; i<data.size()-1; i++){
            res = res + std::to_string(data[i]) + std::string(" ");
        }
        res += std::to_string(data[i]);
        return res;
    }

    // 字符串转double容器
    // 将字符串按照分隔符分割，转化为double类型，放在容器中
    std::vector<double> StringToDoubleVector(const std::string& str, const char split)
    {
        std::vector<double> local_model;
        if (str == "") return local_model;
        //在字符串末尾也加入分隔符，方便截取最后一段
        std::string strs = str + split;
        size_t pos = strs.find(split);

        // 若找不到内容则字符串搜索函数返回 npos
        while (pos != strs.npos)
        {
            std::string temp = strs.substr(0, pos);
            local_model.push_back(std::stod(temp));
            //去掉已分割的字符串,在剩下的字符串中进行分割
            strs = strs.substr(pos + 1, strs.size());
            pos = strs.find(split);
        }
        return local_model;
    }

};


INTERFACE_EXPORT(CoTrainContract, (Init)(upload)(download)(addClient)(getStatus)); // 定义的合约abi要在这里增加申明