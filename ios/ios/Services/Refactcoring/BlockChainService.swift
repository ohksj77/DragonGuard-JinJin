//
//  BlockChainService.swift
//  ios
//
//  Created by 정호진 on 2023/06/22.
//

import Foundation
import RxSwift
import Alamofire

final class BlockChainService{
    
    func getData() -> Observable<[BlockChainListModel]>{
        let url = APIURL.apiUrl.getBlockChain(ip: APIURL.ip)
        let access = UserDefaults.standard.string(forKey: "Access")
        
        return Observable.create { observer in
            AF.request(url,
                       method: .get,
                       headers: ["Content-type": "application/json",
                                     "Authorization": "Bearer \(access ?? "")"])
            .responseDecodable(of: [BlockChainListModel].self) { response in
                switch response.result{
                case .success(let data):
                    observer.onNext(data)
                case .failure(let error):
                    print("BlockChainService error!\n\(error)")
                }

            }
            
            return Disposables.create()
        }
        
    }
    
}
