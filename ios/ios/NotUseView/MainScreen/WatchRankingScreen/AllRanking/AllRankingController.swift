////
////  AllRankingController.swift
////  ios
////
////  Created by 정호진 on 2023/02/01.
////
//
//import Foundation
//import UIKit
//import RxCocoa
//import RxSwift
//
//// 전체 랭킹
//final class AllRankingController: UIViewController{
//
//    let deviceHeight = UIScreen.main.bounds.height
//    let disposeBag = DisposeBag()
//    let userInfoViewModel = ALLUserInfoViewModel()
//    var rankingList = [UserInfoModel]()
//
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        self.view.backgroundColor = .white
//        self.navigationController?.navigationBar.isHidden = false
//        self.navigationItem.title = "전체 랭킹"
//        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.font: UIFont(name: "IBMPlexSansKR-SemiBold", size: 20)!, .foregroundColor: UIColor.black]
//
//    }
//
//    override func viewWillAppear(_ animated: Bool) {
//
//        self.getData()
//    }
//
//    /*
//     UI 작성
//     */
//
//    lazy var repoTableView: UITableView = {
//        let repoTableView = UITableView()
//        repoTableView.backgroundColor = .white
//        return repoTableView
//    }()
//
//    /*
//     UI 추가할 때 작성하는 함수
//     */
//
//    
//    //View에 적용할 때 사용하는 함수
//    private func addUItoView(){
//        self.view.addSubview(repoTableView)   //tableview 적용
//
//        // 결과 출력하는 테이블 뷰 적용
//        self.repoTableView.dataSource = self
//        self.repoTableView.delegate = self
//
//        // tableview 설치
//        self.repoTableView.register(WatchRankingTableView.self, forCellReuseIdentifier: WatchRankingTableView.identifier)
//        settingAutoLayout()
//    }
//
//    private func getData(){
//        self.rankingList = []
//        self.userInfoViewModel.getAllRanking()
//            .subscribe(onNext: { rankingList in
//                for info in rankingList{
//                    self.rankingList.append(info)
//                }
//                self.addUItoView()
//                self.repoTableView.reloadData()
//            })
//            .disposed(by: self.disposeBag)
//    }
//
//    /*
//     UI AutoLayout 코드 작성
//
//     함수 실행시 private으로 시작할 것 (추천)
//     */
//
//    private func settingAutoLayout(){
//        repoTableView.snp.makeConstraints({ make in
//            make.top.equalTo(60)
//            make.leading.equalTo(20)
//            make.trailing.equalTo(-20)
//            make.bottom.equalTo(-30)
//        })
//
//    }
//}
//
//extension AllRankingController: UITableViewDelegate, UITableViewDataSource {
//
//    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//        let cell = tableView.dequeueReusableCell(withIdentifier: WatchRankingTableView.identifier, for: indexPath) as? WatchRankingTableView ?? WatchRankingTableView()
//
//        cell.prepare(rank: indexPath.section + 1, text: self.rankingList[indexPath.section].githubId, count: self.rankingList[indexPath.section].tokens)
//
//        cell.layer.cornerRadius = 15
//        cell.backgroundColor = UIColor(red: 153/255.0, green: 204/255.0, blue: 255/255.0, alpha: 0.4)
//        cell.layer.borderWidth = 1
//
//        return cell
//    }
//
//    // tableview cell이 선택된 경우
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//
//        tableView.deselectRow(at: indexPath, animated: true)
//    }
//
//    func scrollViewDidScroll(_ scrollView: UIScrollView) {
//        let position = scrollView.contentOffset.y
//
//        if position > (repoTableView.contentSize.height - scrollView.frame.size.height){
//            if self.rankingList.count % 30 == 0{
//                self.getData()
//            }
//        }
//    }
//
//    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat { return deviceHeight / 10 }
//
//    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? { return " " }
//
//    // section 간격 설정
//    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {  return 1 }
//
//    // section 내부 cell 개수
//    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { return 1 }
//
//    // section 개수
//    func numberOfSections(in tableView: UITableView) -> Int { return self.rankingList.count }
//}
//
