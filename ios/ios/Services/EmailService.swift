//
//  EmailService.swift
//  ios
//
//  Created by 정호진 on 2023/03/29.
//

import Foundation
import RxSwift
import Alamofire

// MARK: 이메일 인증 관련 서비스
final class EmailService{
            
    // MARK: 이메일 인증번호 유효한지 확인하는 함수
    /// - Parameters:
    ///   - id: 이메일 아이디
    ///   - code: 인증 번호
    /// - Returns: true, false
    func checkValidNumber(id: Int, code: Int, organization_id: Int) -> Observable<Bool>{
        let url = APIURL.apiUrl.checkEmailValidCode(ip: APIURL.ip,
                                                    id: id,
                                                    code: code,
                                                    organization_id: organization_id)
        let access = UserDefaults.standard.string(forKey: "Access")
        print(url)
        
        return Observable.create { observer in
            
            AF.request(url,
                       method: .get,
                       headers: ["Authorization": "Bearer \(access ?? "")"])
            .responseDecodable(of: CheckEmailModel.self){ response in
                switch response.result{
                case .success(let data):
                    observer.onNext(data.is_valid_code)
                case .failure(let error):
                    print("checkValidNumber error! \(error)")
                }
            }
        
            return Disposables.create()
        }
    }
    
    // MARK: 인증번호 삭제
    func removeCertificatedNumber(){
        let url = APIURL.apiUrl.removeCertificatedNumber(ip: APIURL.ip)
        let access = UserDefaults.standard.string(forKey: "Access")

        AF.request(url,
                   method: .delete,
                   headers: [
                   "Content-Type" : "application/json",
                   "Authorization" : "Bearer \(access ?? "")"
                   ])
        .response{ response in
            switch response.result {
            case .success(_):
                print("success")
            case .failure(let error):
                print("removeCertificatedNumber error! \(error)")
            }
        }
    }
}
