//
//  CompareService.swift
//  ios
//
//  Created by 정호진 on 2023/02/24.
//

import Foundation
import Alamofire
import RxSwift

final class CompareService{
    let ip = APIURL.ip
    
    /// 선택된 레포지토리들을 보내 유저 정보들을 받아오는 함수
    /// - Parameters:
    ///   - firstRepo: 첫 번째 선택된 레포지토리
    ///   - secondRepo: 두 번째 선택된 레포지토리
    func beforeSendingInfo(firstRepo: String, secondRepo: String) -> Observable<CompareUserModel> {
        let url = APIURL.apiUrl.compareBeforeAPI(ip: ip)
        let body = ["first_repo": firstRepo, "second_repo": secondRepo]
        let access = UserDefaults.standard.string(forKey: "Access")
        
        print("beforeSendingInfo\n\(url)")
        print("body \(body)")
        return Observable.create(){ observer in
            
                AF.request(url,
                           method: .post,
                           parameters: body,
                           encoding: JSONEncoding(options: []),
                           headers: ["Content-type": "application/json",
                                     "Authorization": "Bearer \(access ?? "")"])
                .validate(statusCode: 200..<201)
                .responseDecodable(of: CompareUserModel.self) { res in
                    
                    switch res.result{
                    case .success(let data):
                        print(data)
                        observer.onNext(data)
                    case .failure(let error):
                        print("beforeSendingInfo error!\n\(error)")
                    }
                    
                }
                
            return Disposables.create()
        }
        
    }
    
    
    
    /// 레포지토리 상세 정보를 요청하는 함수
    /// - Parameters:
    ///   - firstRepo: 첫 번쨰 선택된 레포 이름
    ///   - secondRepo: 두 번쨰 선택된 레포 이름
    func getCompareInfo(firstRepo: String, secondRepo: String) -> Observable<CompareRepoModel> {
        let url = APIURL.apiUrl.compareRepoAPI(ip: ip)
        let body = ["first_repo": firstRepo, "second_repo": secondRepo]
        let access = UserDefaults.standard.string(forKey: "Access")
        print("getCompareInfo\n\(url)")
        print("body \(body)")
        return Observable.create() { observer in
                AF.request(url,
                           method: .post,
                           parameters: body,
                           encoding: JSONEncoding.default,
                           headers: ["Content-type": "application/json",
                                     "Authorization": "Bearer \(access ?? "")"])
                .validate(statusCode: 200..<506)
                .responseJSON { res in
                    print(res)
                }
//                .responseDecodable(of: CompareRepoDecodingModel.self) { response in
//                    print("response REPO: \(response)")
//                    guard let responseResult = response.value else {return}
//
//                    if responseResult.firstRepo.gitRepo.full_name != "" && responseResult.secondRepo.gitRepo.full_name != ""{
//                        // first Repo
//                        var firstLanguage: [String] = []
//                        var firstCount: [Int] = []
//                        for lang in responseResult.firstRepo.languages.language{
//                            firstLanguage.append(lang)
//                        }
//                        for cnt in responseResult.firstRepo.languages.count{
//                            firstCount.append(cnt)
//                        }
//
//                        // Second Repo
//                        var secondLanguage: [String] = []
//                        var secondCount: [Int] = []
//                        for lang in responseResult.secondRepo.languages.language{
//                            secondLanguage.append(lang)
//                        }
//                        for cnt in responseResult.secondRepo.languages.count{
//                            secondCount.append(cnt)
//                        }
//
//                        let compareRepo = CompareRepoModel(
//                            firstRepo:  FirstRepoModel(gitRepo: GitRepoModel(full_name: responseResult.firstRepo.gitRepo.full_name,
//                                                                             forks_count: responseResult.firstRepo.gitRepo.forks_count ?? 0,
//                                                                             stargazers_count: responseResult.firstRepo.gitRepo.stargazers_count ?? 0,
//                                                                             watchers_count: responseResult.firstRepo.gitRepo.watchers_count ?? 0,
//                                                                             open_issues_count: responseResult.firstRepo.gitRepo.open_issues_count ?? 0,
//                                                                             closed_issues_count: responseResult.firstRepo.gitRepo.closed_issues_count ?? 0,
//                                                                             subscribers_count: responseResult.firstRepo.gitRepo.subscribers_count ?? 0),
//                                                       statistics: StatisticsModel(commitStats: StatisticsStatsModel(count: responseResult.firstRepo.statistics.commitStats.count ?? 0,
//                                                                                                                     sum: responseResult.firstRepo.statistics.commitStats.sum ?? 0,
//                                                                                                                     min: responseResult.firstRepo.statistics.commitStats.min ?? 0,
//                                                                                                                     max: responseResult.firstRepo.statistics.commitStats.max ?? 0,
//                                                                                                                     average: responseResult.firstRepo.statistics.commitStats.average ?? 0),
//                                                                                   additionStats: StatisticsStatsModel(count: responseResult.firstRepo.statistics.additionStats.count ?? 0,
//                                                                                                                       sum: responseResult.firstRepo.statistics.additionStats.sum ?? 0,
//                                                                                                                       min: responseResult.firstRepo.statistics.additionStats.min ?? 0,
//                                                                                                                       max: responseResult.firstRepo.statistics.additionStats.max ?? 0,
//                                                                                                                       average: responseResult.firstRepo.statistics.additionStats.average ?? 0),
//                                                                                   deletionStats: StatisticsStatsModel(count: responseResult.firstRepo.statistics.deletionStats.count ?? 0,
//                                                                                                                       sum: responseResult.firstRepo.statistics.deletionStats.sum ?? 0,
//                                                                                                                       min: responseResult.firstRepo.statistics.deletionStats.min ?? 0,
//                                                                                                                       max: responseResult.firstRepo.statistics.deletionStats.max ?? 0,
//                                                                                                                       average: responseResult.firstRepo.statistics.deletionStats.average ?? 0)),
//                                                       languages: LanguagesModel(language: firstLanguage, count: firstCount),
//                                                       languagesStats: StatisticsStatsModel(count: responseResult.firstRepo.languagesStats.count ?? 0,
//                                                                                            sum: responseResult.firstRepo.languagesStats.sum ?? 0,
//                                                                                            min: responseResult.firstRepo.languagesStats.min ?? 0,
//                                                                                            max: responseResult.firstRepo.languagesStats.max ?? 0,
//                                                                                            average: responseResult.firstRepo.languagesStats.average ?? 0),
//                                                       profileUrls: responseResult.firstRepo.profileUrls),
//                            secondRepo: secondRepoModel(gitRepo: GitRepoModel(full_name: responseResult.secondRepo.gitRepo.full_name,
//                                                                              forks_count: responseResult.secondRepo.gitRepo.forks_count ?? 0,
//                                                                              stargazers_count: responseResult.secondRepo.gitRepo.stargazers_count ?? 0,
//                                                                              watchers_count: responseResult.secondRepo.gitRepo.watchers_count ?? 0,
//                                                                              open_issues_count: responseResult.secondRepo.gitRepo.open_issues_count ?? 0,
//                                                                              closed_issues_count: responseResult.secondRepo.gitRepo.closed_issues_count ?? 0,
//                                                                              subscribers_count: responseResult.secondRepo.gitRepo.subscribers_count ?? 0),
//                                                        statistics: StatisticsModel(commitStats: StatisticsStatsModel(count: responseResult.secondRepo.statistics.commitStats.count ?? 0,
//                                                                                                                      sum: responseResult.secondRepo.statistics.commitStats.sum ?? 0,
//                                                                                                                      min: responseResult.secondRepo.statistics.commitStats.min ?? 0,
//                                                                                                                      max: responseResult.secondRepo.statistics.commitStats.max ?? 0,
//                                                                                                                      average: responseResult.secondRepo.statistics.commitStats.average ?? 0),
//                                                                                    additionStats: StatisticsStatsModel(count: responseResult.secondRepo.statistics.additionStats.count ?? 0,
//                                                                                                                        sum: responseResult.secondRepo.statistics.additionStats.sum ?? 0,
//                                                                                                                        min: responseResult.secondRepo.statistics.additionStats.min ?? 0,
//                                                                                                                        max: responseResult.secondRepo.statistics.additionStats.max ?? 0,
//                                                                                                                        average: responseResult.secondRepo.statistics.additionStats.average ?? 0),
//                                                                                    deletionStats: StatisticsStatsModel(count: responseResult.secondRepo.statistics.deletionStats.count ?? 0,
//                                                                                                                        sum: responseResult.secondRepo.statistics.deletionStats.sum ?? 0,
//                                                                                                                        min: responseResult.secondRepo.statistics.deletionStats.min ?? 0,
//                                                                                                                        max: responseResult.secondRepo.statistics.deletionStats.max ?? 0,
//                                                                                                                        average: responseResult.secondRepo.statistics.deletionStats.average ?? 0)),
//                                                        languages: LanguagesModel(language: secondLanguage, count: secondCount),
//                                                        languagesStats: StatisticsStatsModel(count: responseResult.secondRepo.languagesStats.count ?? 0,
//                                                                                             sum: responseResult.secondRepo.languagesStats.sum ?? 0,
//                                                                                             min: responseResult.secondRepo.languagesStats.min ?? 0,
//                                                                                             max: responseResult.secondRepo.languagesStats.max ?? 0,
//                                                                                             average: responseResult.secondRepo.languagesStats.average ?? 0),
//                                                        profileUrls: responseResult.secondRepo.profileUrls))
//                        observer.onNext(compareRepo)
//
//                    }
//                }
            return Disposables.create()
        }
        
    }
    
}
