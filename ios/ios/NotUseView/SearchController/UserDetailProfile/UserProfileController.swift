////
////  UserProfileController.swift
////  ios
////
////  Created by 정호진 on 2023/04/13.
////
//
//import Foundation
//import UIKit
//import SnapKit
//import RxSwift
//
//// MARK: 검색한 사용자 프로필 상세 조회
//final class UserProfileController: UIViewController{
//    private let imageView = UIImageView()
//    private var repoList: [String] = []
//    private let disposeBag = DisposeBag()
//    var githubId: String?
//    
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        
//        self.view.backgroundColor = UIColor(red: 127/255, green: 78/255, blue: 31/255, alpha: 1.0) /* #7f4e1f */
//    
////        addUIToView()
//    }
//    
//    override func viewWillAppear(_ animated: Bool) {
//        getData()
//    }
//    
//    override func viewDidLayoutSubviews() {
//        super.viewDidLayoutSubviews()
//
////        storeView.setGradient(color1: UIColor(red: 245/255, green: 245/255, blue: 220/255, alpha: 1.0),
////                              color2: UIColor(red: 150/255, green: 75/255, blue: 0/255, alpha: 1.0),
////                              zPosition: -1,
////                              cornerRadius: 20)
//        
//    }
//    
//    // MARK: safe Area 색칠 view
//    private lazy var safeAreaColorView: UIView = {
//        let view = UIView()
//        view.backgroundColor = UIColor(red: 127/255, green: 78/255, blue: 31/255, alpha: 1.0)
//        return view
//    }()
//    
//    // MARK: 유저 프로필 버튼
//    private lazy var profileBtn: UIButton = {
//        let btn = UIButton()
//        btn.setImage(imageView.image, for: .normal)
//        btn.imageView?.layer.cornerRadius = 20
//        btn.setTitleColor(.black, for: .normal)
//        btn.titleLabel?.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 20)
//        return btn
//    }()
//    
//    // MARK: 전체 랭킹 타이틀 '나무꾼 랭킹'
//    private lazy var titleRanking: UILabel = {
//        let label = UILabel()
//         label.text = "나무꾼 랭킹"
//         label.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 23)
//         return label
//    }()
//    
//    // MARK: 전체 랭킹, 나무꾼 랭킹
//    private lazy var userRanking: UILabel = {
//        let label = UILabel()
//         label.text = "1"
//         label.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 20)
//         return label
//    }()
//    
//    // MARK: 창고 뷰 테두리
//    private lazy var sideStoreView: UIView = {
//        let view = UIView()
//        view.backgroundColor = .clear
//        return view
//    }()
//    
//    // MARK: '창고' 라벨
//    private lazy var storeTitleLabel: UILabel = {
//       let label = UILabel()
//        label.text = " 창고 "
//        label.backgroundColor = UIColor(red: 245/255, green: 245/255, blue: 220/255, alpha: 1.0)
//        label.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 25)
//        return label
//    }()
//    
//    // MARK: 창고 뷰
//    private lazy var storeView: StoreView = {
//        let view = StoreView()
//        view.layer.cornerRadius = 20
//        view.layer.borderWidth = 1
//        view.backgroundColor = UIColor(red: 245/255, green: 245/255, blue: 220/255, alpha: 1.0)
//        return view
//    }()
//    
//    // MARK: 소유한 레포지토리 테두리
//    private lazy var siderepoView: UIView = {
//        let view = UIView()
//        view.backgroundColor = .clear
//        return view
//    }()
//    
//    // MARK: '소유한 숲' 보여줄 라벨
//    private lazy var repoTitleLabel: UILabel = {
//        let label = UILabel()
//         label.text = "  소유한 숲  "
//         label.backgroundColor = UIColor(red: 245/255, green: 245/255, blue: 220/255, alpha: 1.0)
//         label.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 25)
//         return label
//    }()
//    
//    // MARK: 소유한 레포지토리 리스트 뷰
//    private lazy var repoView: UIView = {
//        let view = UIView()
//        view.layer.borderWidth = 1
//        view.layer.cornerRadius = 20
//        view.backgroundColor = UIColor(red: 245/255, green: 245/255, blue: 220/255, alpha: 1.0)
//        return view
//    }()
//    
//    // MARK: 소유한 레포지토리 tableView
//    private lazy var repoTableView: UITableView = {
//        let tableview = UITableView()
//        tableview.separatorStyle = .none
//        tableview.backgroundColor = .clear
//        return tableview
//    }()
//    
//    
//    /*
//     Add UI & AutoLayout
//     */
//    
//    private func addUIToView(){
//        self.view.addSubview(safeAreaColorView)
//        
//        self.view.addSubview(profileBtn)
//        
//        self.view.addSubview(titleRanking)
//        self.view.addSubview(userRanking)
//        
//        self.view.addSubview(sideStoreView)
//        sideStoreView.addSubview(storeView)
//        sideStoreView.addSubview(storeTitleLabel)
//        
//        self.view.addSubview(siderepoView)
//        siderepoView.addSubview(repoView)
//        siderepoView.addSubview(repoTitleLabel)
//        
//        repoView.addSubview(repoTableView)
//        repoTableView.dataSource = self
//        repoTableView.delegate = self
//        repoTableView.register(RepoTableViewCell.self, forCellReuseIdentifier: RepoTableViewCell.identifier)
//        
//        setAutoLayout()
//    }
//    
//    private func setAutoLayout(){
//        
//        safeAreaColorView.snp.makeConstraints { make in
//            make.top.equalTo(self.view.safeAreaLayoutGuide).offset(-60)
//            make.leading.trailing.equalTo(0)
//            make.bottom.equalTo(self.view.safeAreaLayoutGuide).offset(60)
//        }
//        
//        profileBtn.snp.makeConstraints { make in
//            make.top.equalTo(self.view.safeAreaLayoutGuide).offset(10)
//            make.leading.equalTo(20)
//        }
//        
//        titleRanking.snp.makeConstraints { make in
//            make.bottom.equalTo(profileBtn.snp.centerY).offset(10)
//            make.trailing.equalTo(-20)
//        }
//        
//        userRanking.snp.makeConstraints { make in
//            make.top.equalTo(titleRanking.snp.bottom)
//            make.centerX.equalTo(titleRanking.snp.centerX)
//        }
//        
//        /// side View
//        sideStoreView.snp.makeConstraints { make in
//            make.top.equalTo(self.profileBtn.snp.bottom).offset(10)
//            make.leading.equalTo(20)
//            make.trailing.equalTo(-20)
//            make.height.equalTo(self.view.safeAreaLayoutGuide.layoutFrame.height*35/100)
//        }
//        
//        /// SubView '창고' 라벨
//        storeTitleLabel.snp.makeConstraints { make in
//            make.top.equalToSuperview()
//            make.centerX.equalToSuperview()
//        }
//        
//        /// SubView custom label
//        storeView.snp.makeConstraints { make in
//            make.top.equalToSuperview().offset(20)
//            make.leading.equalToSuperview().offset(10)
//            make.bottom.equalToSuperview().offset(-10)
//            make.trailing.equalToSuperview().offset(-10)
//        }
//        
//        /// repo list 담을 view
//        siderepoView.snp.makeConstraints { make in
//            make.top.equalTo(sideStoreView.snp.bottom).offset(30)
//            make.leading.equalTo(20)
//            make.trailing.equalTo(-20)
//            make.bottom.equalTo(self.view.safeAreaLayoutGuide)
//        }
//        
//        repoView.snp.makeConstraints { make in
//            make.top.equalTo(siderepoView.safeAreaLayoutGuide).offset(20)
//            make.leading.equalTo(siderepoView.safeAreaLayoutGuide)
//            make.trailing.equalTo(siderepoView.safeAreaLayoutGuide)
//            make.bottom.equalTo(siderepoView.safeAreaLayoutGuide)
//        }
//        /// '소유한 숲' 라벨
//        repoTitleLabel.snp.makeConstraints { make in
//            make.top.equalTo(siderepoView.snp.top)
//            make.centerX.equalToSuperview()
//        }
//        
//        repoTableView.snp.makeConstraints { make in
//            make.top.equalTo(repoView.safeAreaLayoutGuide).offset(20)
//            make.leading.equalTo(20)
//            make.trailing.equalTo(-20)
//            make.bottom.equalTo(repoView.safeAreaLayoutGuide).offset(-10)
//        }
//        
//    }
//    
//    private func getData(){
//        MyProfileviewModel.viewModel
//            .getMyProfile(githubId: self.githubId ?? "")
//            .subscribe(onNext: { data in
//                
//                self.profileBtn.setTitle(self.githubId, for: .normal)
//                
//            })
//            .disposed(by: self.disposeBag)
//
//    }
//    
//}
//
//// MARK: 소유한 레포지토리 리스트 출력하는 tableview
//extension UserProfileController: UITableViewDelegate, UITableViewDataSource{
//    
//    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//        let cell = tableView.dequeueReusableCell(withIdentifier: RepoTableViewCell.identifier,for: indexPath) as! RepoTableViewCell
//        cell.backgroundColor = UIColor(red: 255/255, green: 194/255, blue: 194/255, alpha: 0.5) /* #ffc2c2 */
//        
//        cell.layer.cornerRadius = 20
//        cell.inputInfo(title: "1")
//        return cell
//    }
//    
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        tableView.deselectRow(at: indexPath, animated: true)
//    }
//    
//    func numberOfSections(in tableView: UITableView) -> Int { return repoList.count }
//    
//    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? { return " " }
//    
//    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat { return 1 }
//    
//    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { return 1 }
//}
//
//extension UIView{
//    func setGradient(color1: UIColor, color2: UIColor, zPosition: CGFloat, cornerRadius: CGFloat){
//        let colors: [CGColor] = [color1.cgColor,
//                                 UIColor(red: 221/255, green: 146/255, blue: 55/255, alpha: 1.0).cgColor,
//                                 ]
//        let gradientLayer = CAGradientLayer()
//        gradientLayer.type = .radial
////        gradientLayer.locations = [0.9]
//        gradientLayer.startPoint = CGPoint(x: 0.5, y: 0.5)
//        gradientLayer.endPoint = CGPoint(x: 1.0, y: 1.0)
//        gradientLayer.zPosition = zPosition
//        gradientLayer.colors = colors
//        gradientLayer.cornerRadius = cornerRadius
//        gradientLayer.frame = bounds
//        layer.addSublayer(gradientLayer)
//    }
//}
//
//
//import SwiftUI
//
//struct VCPreViewUserProfileController:PreviewProvider {
//    static var previews: some View {
//        UserProfileController().toPreview().previewDevice("iPhone 14 Pro")
//        // 실행할 ViewController이름 구분해서 잘 지정하기
//    }
//}
//struct VCPreViewUserProfileController2:PreviewProvider {
//    static var previews: some View {
//        UserProfileController().toPreview().previewDevice("iPhone 11")
//        // 실행할 ViewController이름 구분해서 잘 지정하기
//    }
//}
