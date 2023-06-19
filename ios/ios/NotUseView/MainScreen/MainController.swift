////
////  ViewController.swift
////  ios
////
////  Created by 정호진 on 2023/01/03.
////
//
//import UIKit
//import SnapKit
//import RxSwift
//
//final class MainController: UIViewController {
//    private let indexBtns = ["전체 사용자 랭킹", "조직내 나의 랭킹", "랭킹 보러가기", "Repository 비교"]
//    private let deviceWidth = UIScreen.main.bounds.width
//    private let deviceHeight = UIScreen.main.bounds.height
//    private let viewModel = MainViewModel()
//    private let disposeBag = DisposeBag()
//    private let img = UIImageView()
//    private var id: Int?
//    private var jwtToken: String?
//    private var rank = 0
//    private var myOrganization: String?
//    private var githubId: String?
//    private var rankingInOrganization: Int?
//
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        self.view.backgroundColor = .white
//        self.navigationController?.navigationBar.isHidden = true    // navigation bar 삭제
//        self.navigationItem.backButtonTitle = "Home"    //다른 화면에서 BackBtn title 설정
//        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
//
//        // UI view에 적용
//        addUItoView()
//
//        // UI AutoLayout 적용
//        settingAutoLayout()
//    }
//
//    override func viewWillAppear(_ animated: Bool) {
//        getMyData() // 내 토큰, 내 티어 데이터 불러오기
//        self.navigationController?.navigationBar.isHidden = true // navigation bar 삭제
//    }
//
//    /*
//     UI 코드 작성
//     */
//
//    // MARK: 버튼들 나열할 collectionView
//    private lazy var collectionView: UICollectionView = {
//        let layout = UICollectionViewFlowLayout()
//        let cv = UICollectionView(frame: .zero, collectionViewLayout: layout)
//        cv.backgroundColor = .white
//        return cv
//    }()
//
//    // MARK: 소속 대학교 이름 label
//    private lazy var univNameLabel: UILabel = {
//        let univName = UILabel()
//        univName.textColor = .black
//        univName.text = "Unknown"
//        univName.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 30)
//        return univName
//    }()
//
//    // MARK: 검색 버튼 UI
//    private lazy var searchUI: UIButton = {
//        let searchUI = UIButton()
//        searchUI.titleColor(for: .normal)
//        searchUI.tintColor = .black
//        searchUI.setImage(UIImage(systemName: "magnifyingglass")?.withRenderingMode(.alwaysTemplate), for: .normal)
//        searchUI.backgroundColor = .lightGray
//        searchUI.setTitle(" Repository or User ", for: .normal)
//        searchUI.titleLabel?.font = UIFont.systemFont(ofSize: 20)
//        searchUI.setTitleColor(.gray, for: .normal)
//        searchUI.addTarget(self, action: #selector(searchUIClicked), for: .touchUpInside)
//        searchUI.layer.cornerRadius = 10
//        return searchUI
//    }()
//
//    // MARK: 내 티어, 토큰 띄우는 UI
//    private lazy var tierTokenUI: TierTokenCustomUIView = {
//        let tierTokenUI = TierTokenCustomUIView()
//        tierTokenUI.backgroundColor  = UIColor(red: 255/255, green: 194/255, blue: 194/255, alpha: 0.5) /* #ffc2c2 */
//        tierTokenUI.layer.cornerRadius = 20
//
//        // 티어, 토큰 개수 입력
//        tierTokenUI.inputText(myTier: "SPROUT", tokens: 0)
//        return tierTokenUI
//    }()
//
//    // MARK: 설정 화면으로 이동
//    private lazy var settingUI: UIButton = {
//        let btn = UIButton()
//
//        btn.setImage(UIImage(systemName: "gearshape.fill")?.resize(newWidth: view.safeAreaLayoutGuide.layoutFrame.width/25), for: .normal)
//        btn.imageView?.layer.cornerRadius = 20
//        btn.titleLabel?.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 20)
//        btn.addTarget(self, action: #selector(settingUIClicked), for: .touchUpInside)
//        return btn
//    }()
//
//    // MARK: 유저 프로필 버튼
//    private lazy var profileBtn: UIButton = {
//        let btn = UIButton()
//
//        btn.setImage(img.image, for: .normal)
//        btn.imageView?.layer.cornerRadius = 20
//        btn.setTitleColor(.black, for: .normal)
//        btn.titleLabel?.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 20)
//        btn.addTarget(self, action: #selector(clickedProfileBtn), for: .touchUpInside)
//        return btn
//    }()
//
//    /*
//     UI Action 작성
//     */
//
//    // MARK: collectionView 설정
//    private func configureCollectionView(){
//        collectionView.register(MainCollectionView.self, forCellWithReuseIdentifier: MainCollectionView.identifier)
//        collectionView.dataSource = self
//        collectionView.delegate = self
//        collectionView.register(MainRankingCollectionView.self, forCellWithReuseIdentifier: MainRankingCollectionView.identifier)
//    }
//
//    // MARK: 검색 버튼 누르는 경우 네비게이션 뷰 방식으로 이동
//    @objc
//    private func searchUIClicked(){
//        let searchPage = SearchViewController()
//        searchPage.beforePage = "Main"
//        searchPage.modalPresentationStyle = .fullScreen
//        self.present(searchPage,animated: true)
//
////        self.navigationItem.backButtonTitle = " "    //다른 화면에서 BackBtn title 설정
////        self.navigationController?.pushViewController(searchPage, animated: true)
//    }
//
//    // MARK: 유저 이름 누르는 경우 네비게이션 뷰 방식으로 이동
//    @objc
//    private func clickedProfileBtn(){
//        print("clicked")
////        self.navigationController?.pushViewController((), animated: true)
//    }
//
//
//    // MARK: 설정 버튼 누르는 경우 네비게이션 뷰 방식으로 이동
//    @objc
//    private func settingUIClicked(){
//        self.navigationController?.pushViewController(SettingController(), animated: true)
//    }
//
//    /*
//     UI 추가할 때 작성하는 함수
//     */
//
//    // MARK:  Add To UI
//    private func addUItoView(){
//        self.view.addSubview(collectionView)
//        self.view.addSubview(univNameLabel)
//        self.view.addSubview(searchUI)
//        self.view.addSubview(settingUI)
//        self.view.addSubview(tierTokenUI)
//        self.view.addSubview(profileBtn)
//        configureCollectionView()
//    }
//
//    /*
//     UI AutoLayout 코드 작성
//
//     함수 실행시 private으로 시작할 것
//     */
//
//    // MARK: Set AutoLayout
//    private func settingAutoLayout(){
//
//        /// 소속 대학교 이름 AutoLayout
//        univNameLabel.snp.makeConstraints({ make in
//            make.top.equalTo(settingUI.snp.bottom).offset(30)
//            make.leading.equalTo(30)
//            make.trailing.equalTo(-30)
//
//        })
//
//        /// 검색 버튼 AutoLayout
//        searchUI.snp.makeConstraints({ make in
//            make.top.equalTo(univNameLabel.snp.bottom).offset(self.view.safeAreaLayoutGuide.layoutFrame.height/20)
//            make.centerX.equalToSuperview()
//            make.width.equalTo(self.view.safeAreaLayoutGuide.layoutFrame.width*2/3)
//        })
//
//        profileBtn.snp.makeConstraints { make in
//            make.top.equalTo(view.safeAreaLayoutGuide).offset(10)
//            make.leading.equalTo(20)
//        }
//
//        /// 설정 버튼 AutoLayout
//        settingUI.snp.makeConstraints({ make in
//            make.top.equalTo(view.safeAreaLayoutGuide).offset(10)
//            make.trailing.equalTo(-20)
//        })
//
//        /// 내 티어, 토큰 띄우는 UI AutoLayout
//        tierTokenUI.snp.makeConstraints({ make in
//            make.top.equalTo(searchUI.snp.bottom).offset(10)
//            make.leading.equalTo(30)
//            make.trailing.equalTo(-30)
//            make.height.equalTo(view.safeAreaLayoutGuide.layoutFrame.height/6)
//            make.bottom.equalTo(collectionView.snp.top).offset(-10)
//        })
//
//
//        /// CollectionView AutoLayout
//        collectionView.snp.makeConstraints({ make in
//            make.leading.equalTo(30)
//            make.trailing.equalTo(-30)
//            make.bottom.equalTo(-30)
//        })
//
//
//    }
//
//    // MARK: 내 티어, 내 토큰 가져오는 함수
//    private func getMyData(){
//        let access = UserDefaults.standard.string(forKey: "Access")
//
//        self.viewModel.getMyInformation(token: access ?? "")
//            .subscribe(onNext: { data in
//                self.rank = data.rank
//                self.tierTokenUI.inputText(myTier: data.tier, tokens: data.tokenAmount)
//                let url = URL(string: data.profileImage ?? "")!
//                self.img.load(img: self.img, url: url,btn: self.profileBtn)
//                self.profileBtn.setTitle(data.githubId, for: .normal)
//                self.githubId = data.githubId
//                self.univNameLabel.text = data.organization
//                self.myOrganization = data.organization
//                self.rankingInOrganization = data.organizationRank
//                self.collectionView.reloadData()
//
//            })
//            .disposed(by: disposeBag)
//    }
//
//}
//
//
//
//// MARK: CollectionView DataSouce, Delegate 설정
//extension MainController: UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout{
//    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
//        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: MainRankingCollectionView.identifier, for: indexPath) as! MainRankingCollectionView
//
//        if indexPath.row == 0 {
//            cell.labelText(indexBtns[indexPath.row], rankingNum: "\(self.rank)", "상위 0%")
//        }
//        else if indexPath.row == 1 {
//            cell.labelText(indexBtns[indexPath.row], rankingNum: "\(self.rankingInOrganization ?? 0)", "상위 0%")
//        }
//        else {
//            cell.labelText("", rankingNum: indexBtns[indexPath.row], "")
//        }
//
//        cell.backgroundColor = UIColor(red: 255/255, green: 194/255, blue: 194/255, alpha: 0.5) /* #ffc2c2 */
//
//
//        cell.layer.cornerRadius = 20    //테두리 둥글게
//        return cell
//    }
//
//    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int { return indexBtns.count }
//
//
//    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
//        let cellHeight = view.safeAreaLayoutGuide.layoutFrame.height*24/100
//        let cellWidth = collectionView.bounds.width*48/100
//
//        return CGSize(width: cellWidth, height: cellHeight)
//    }
//
//    // MARK:  cell 선택되었을 때
//    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
//        switch indexPath.row{
//        case 2:
//            let watchRanking = WatchRankingController()
//            watchRanking.myOrganization = self.myOrganization
//            watchRanking.githubId = self.githubId
//            self.navigationController?.pushViewController(watchRanking, animated: true)
//        case 3:
//            self.navigationController?.pushViewController(CompareController(), animated: true)
//        default:
//            return
//        }
//    }
//
//
//}
//
//
//
//
///*
// SwiftUI preview 사용하는 코드
//
// preview 실행이 안되는 경우 단축키
// Command + Option + Enter : preview 그리는 캠버스 띄우기
// Command + Option + p : preview 재실행
// */
//
//import SwiftUI
//
//struct VCPreViewMain:PreviewProvider {
//    static var previews: some View {
//        MainController().toPreview().previewDevice("iPhone 14 Pro")
//        // 실행할 ViewController이름 구분해서 잘 지정하기
//    }
//}
//struct VCPreViewMain2:PreviewProvider {
//    static var previews: some View {
//        MainController().toPreview().previewDevice("iPad (10th generation)")
//        // 실행할 ViewController이름 구분해서 잘 지정하기
//    }
//}
