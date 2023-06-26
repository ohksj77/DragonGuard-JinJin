////
////  MyOrganizationList.swift
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
//final class MyOraganizationListController: UIViewController{
//    
//    let disposeBag = DisposeBag()
//    
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        self.view.backgroundColor = .white
//        
//        self.navigationController?.navigationBar.isHidden = false
//        self.navigationItem.title = "내 Organization 랭킹"
//        
//        addUItoView()
//        settingAutoLayout()
//        
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
//        // datasource는 reactive 적용
//        self.repoTableView.delegate = self
//        
//        // tableview 설치
//        self.repoTableView.register(WatchRankingTableView.self, forCellReuseIdentifier: WatchRankingTableView.identifier)
//        
//    }
//    
//    /*
//     UI AutoLayout 코드 작성
//     
//     함수 실행시 private으로 시작할 것 (추천)
//     */
//    
//    private func settingAutoLayout(){
//        
//        repoTableView.snp.makeConstraints({ make in
//            make.top.equalTo(60)
//            make.leading.equalTo(20)
//            make.trailing.equalTo(-20)
//            make.bottom.equalTo(-30)
//        })
//        
//    }
//    
//}
//
//
//extension MyOraganizationListController: UITableViewDelegate {
//    // tableview cell이 선택된 경우
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        print("selected \(indexPath.section)")
//        tableView.deselectRow(at: indexPath, animated: true)
//    }
//    
//    // section 간격 설정
//    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {  return 1 }
//    
//}
//
//
//
///*
// SwiftUI preview 사용 코드      =>      Autolayout 및 UI 배치 확인용
// 
// preview 실행이 안되는 경우 단축키
// Command + Option + Enter : preview 그리는 캠버스 띄우기
// Command + Option + p : preview 재실행
// */
//
//import SwiftUI
//
//struct VCPreViewOrganiRanking:PreviewProvider {
//    static var previews: some View {
//        MyOraganizationListController().toPreview().previewDevice("iPhone 14 pro")
//        // 실행할 ViewController이름 구분해서 잘 지정하기
//    }
//}
//
//
//
//
