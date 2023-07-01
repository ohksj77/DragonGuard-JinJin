//
//  AllRankingController.swift
//  ios
//
//  Created by 정호진 on 2023/06/17.
//

import Foundation
import UIKit
import SnapKit
import RxSwift
import Lottie

// MARK: 전체 사용자 랭킹
final class AllUserRankingController: UIViewController{
    private let rankingViewModel = RankingViewModel()
    private let disposeBag = DisposeBag()
    private let selectionList: [String] = ["사용자 전체", "조직 전체" ,"회사", "대학교", "고등학교", "ETC"]
    private var topTierData: [AllUserRankingModel] = []
    private var userTierData: [AllUserRankingModel] = []
    private var topTierTypeOfRankingData: [TypeRankingModel] = []
    private var userTierTypeOfRankingData: [TypeRankingModel] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
//        addUIIndicator()
        collectionViewAdd()
        
    }
    
    /*
     UI code
     */
    
    // MARK: 랭킹 종류 선택하는 collectionview
    private lazy var selectionCollectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        let cv = UICollectionView(frame: .zero, collectionViewLayout: layout)
        cv.backgroundColor = .white
        cv.showsHorizontalScrollIndicator = false
        return cv
    }()
    
    // MARK: 스크롤 뷰
    private lazy var scrollView: UIScrollView = {
        let scroll = UIScrollView()
        scroll.backgroundColor = .white
        return scroll
    }()
    
    // MARK:
    private lazy var contentView: UIView = {
        let view = UIView()
        view.backgroundColor = .white
        return view
    }()
    
    // MARK: 상위 랭킹 보여주는 뷰
    private lazy var topView: CustomTopView = {
        let view = CustomTopView()
        view.backgroundColor = .white
        view.layer.shadowOpacity = 1
        view.layer.shadowOffset = CGSize(width: 0, height: 3)
        view.layer.shadowColor = .init(red: 200/255, green: 200/255, blue: 200/255, alpha: 1)
        return view
    }()
    
    // MARK: 4등 부터 나타낼 tableview
    private lazy var tableView: UITableView = {
        let table = UITableView()
        table.isScrollEnabled = false
        table.separatorStyle = .none
        return table
    }()
    
    // MARK: 로딩 UI
    private lazy var indicatorView: LottieAnimationView = {
        let view = LottieAnimationView(name: "graphLottie")
        view.center = self.view.center
        view.loopMode = .loop
        return view
    }()
    
    /*
     Add UI and AutoLayout
     */
    
    // MARK: Add UI Indicator
    private func addUIIndicator(){
        self.view.addSubview(indicatorView)
        setIndicactorAutoLayout()
        indicatorView.play()
    }
    
    // MARK: Indicator View AutoLayout
    private func setIndicactorAutoLayout(){
        indicatorView.snp.makeConstraints { make in
            make.leading.equalTo(self.view.safeAreaLayoutGuide).offset(40)
            make.trailing.equalTo(self.view.safeAreaLayoutGuide).offset(-40)
            make.centerY.equalToSuperview()
        }
    }
    
    // MARK: CollectionView Add
    private func collectionViewAdd(){
        view.addSubview(selectionCollectionView)
        selectionCollectionView.dataSource = self
        selectionCollectionView.delegate = self
        selectionCollectionView.register(SelectionCollectionViewCell.self, forCellWithReuseIdentifier: SelectionCollectionViewCell.identfier)
        collectionViewAutoLayout()
    }
    
    // MARK: CollectionView AutoLayout
    private func collectionViewAutoLayout(){
        selectionCollectionView.snp.makeConstraints { make in
            make.top.equalTo(view.safeAreaLayoutGuide)
            make.leading.equalTo(view.safeAreaLayoutGuide).offset(10)
            make.trailing.equalTo(view.safeAreaLayoutGuide).offset(-10)
            make.height.equalTo(view.safeAreaLayoutGuide.layoutFrame.width/10)
        }
    }
    
    // MARK: 로딩 후 뷰 생성
    private func addUI_AutoLayout_About_Ranking(){
        view.addSubview(scrollView)
        scrollView.addSubview(contentView)
        contentView.addSubview(topView)
        contentView.addSubview(tableView)
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(AllUserTableviewCell.self, forCellReuseIdentifier: AllUserTableviewCell.identifier)
        setAutoLayout()
    }
    
    // MARK: AutoLayout After loading
    private func setAutoLayout(){
 
        scrollView.snp.makeConstraints { make in
            make.top.equalTo(selectionCollectionView.snp.bottom)
            make.leading.trailing.equalTo(view.safeAreaLayoutGuide)
            make.bottom.equalToSuperview()
        }
        
        contentView.snp.makeConstraints { make in
            make.top.equalTo(scrollView.snp.top)
            make.leading.equalTo(scrollView.snp.leading)
            make.trailing.equalTo(scrollView.snp.trailing)
            make.bottom.equalTo(scrollView.snp.bottom)
            make.width.equalTo(scrollView.snp.width)
        }
        
        topView.snp.makeConstraints { make in
            make.top.equalTo(contentView.snp.top)
            make.leading.equalTo(contentView.snp.leading)
            make.trailing.equalTo(contentView.snp.trailing)
            make.height.equalTo(view.safeAreaLayoutGuide.layoutFrame.height/3)
        }
        
        tableView.snp.makeConstraints { make in
            make.top.equalTo(topView.snp.bottom).offset(5)
            make.leading.equalTo(contentView.snp.leading)
            make.trailing.equalTo(contentView.snp.trailing)
            make.bottom.equalTo(contentView.snp.bottom)
            let height = view.safeAreaLayoutGuide.layoutFrame.height/6
            
            if Int(height)*(userTierData.count-1)+10 > 0{
                print("height \(Int(height)*(userTierData.count-1)+10)")
                make.height.equalTo(Int(height)*(userTierData.count-1)+10)
            }
        }
        
    }
    
    /*
     Get Data
     */
    
    // MARK: 전체 사용자 랭킹 데이터 가져옴
    private func loadAllRankingData(){
        scrollView.removeFromSuperview()
        rankingViewModel.getAllRanking(check: true)
            .subscribe(onNext:{ list in
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierData.append(list[i])
                        }
                        else{
                            self.userTierData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.topTierData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
        
    }

    // MARK: 전체 조직 랭킹 데이터 가져옴
    private func loadAllOrganizationData(){
        scrollView.removeFromSuperview()

        rankingViewModel.allRankingOfType(check: true)
            .subscribe(onNext:{ list in
                print("loadAllOrganizationData\n\(list)")
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierTypeOfRankingData.append(list[i])
                        }
                        else{
                            self.userTierTypeOfRankingData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierTypeOfRankingData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.userTierTypeOfRankingData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
    }

    // MARK: 회사 데이터 가져옴
    private func loadCompanyData(){
        
        scrollView.removeFromSuperview()
        
        rankingViewModel.rankingOfType(type: "COMPANY", check: true)
            .subscribe(onNext:{ list in
                print("loadCompanyData\n\(list)")
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierTypeOfRankingData.append(list[i])
                        }
                        else{
                            self.userTierTypeOfRankingData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierTypeOfRankingData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.userTierTypeOfRankingData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
    }
    
    // MARK: 대학교 데이터 가져옴
    private func loadUniversityData(){
        scrollView.removeFromSuperview()
        
        rankingViewModel.rankingOfType(type: "UNIVERSITY", check: true)
            .subscribe(onNext:{ list in
                print("loadUniversityData\n\(list)")
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierTypeOfRankingData.append(list[i])
                        }
                        else{
                            self.userTierTypeOfRankingData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierTypeOfRankingData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.userTierTypeOfRankingData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
    }
    
    // MARK: 고등학교 데이터 가져옴
    private func loadHighSchoolData(){
        scrollView.removeFromSuperview()
        
        rankingViewModel.rankingOfType(type: "HIGH_SCHOOL", check: true)
            .subscribe(onNext:{ list in
                print("loadHighSchoolData\n\(list)")
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierTypeOfRankingData.append(list[i])
                        }
                        else{
                            self.userTierTypeOfRankingData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierTypeOfRankingData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.userTierTypeOfRankingData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
    }
    
    // MARK: ETC 데이터 가져옴
    private func loadETCData(){
        scrollView.removeFromSuperview()
        
        rankingViewModel.rankingOfType(type: "ETC", check: true)
            .subscribe(onNext:{ list in
                print("loadETCData\n\(list)")
                if list.count > 3{
                    for i in 0..<list.count{
                        if(i<3){
                            self.topTierTypeOfRankingData.append(list[i])
                        }
                        else{
                            self.userTierTypeOfRankingData.append(list[i])
                        }
                    }
                }
                else{
                    for i in 0..<list.count{
                        self.topTierTypeOfRankingData.append(list[i])
                    }
                }
                
                self.addUI_AutoLayout_About_Ranking()
                self.topView.getData(list: self.userTierTypeOfRankingData)
                self.tableView.reloadData()
            })
            .disposed(by: disposeBag)
    }
    
}


extension AllUserRankingController: UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: AllUserTableviewCell.identifier, for: indexPath) as? AllUserTableviewCell else { return UITableViewCell()}
        cell.inputData(rank: indexPath.row+4, userData: userTierData[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return userTierData.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return view.safeAreaLayoutGuide.layoutFrame.height/6
    }
    
}

extension AllUserRankingController: UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout{
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SelectionCollectionViewCell.identfier, for: indexPath) as? SelectionCollectionViewCell else { return UICollectionViewCell() }
        cell.inputData(text: selectionList[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        if indexPath.row == 0{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            loadAllRankingData()
        }
        else if indexPath.row == 1{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            addUIIndicator()
            loadAllOrganizationData()
        }
        else if indexPath.row == 2{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            loadCompanyData()
        }
        else if indexPath.row == 3{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            loadUniversityData()
        }
        else if indexPath.row == 4{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            loadHighSchoolData()
        }
        else if indexPath.row == 5{
            scrollView.removeFromSuperview()
            userTierData = []
            topTierData = []
            loadETCData()
        }
        
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {

        return CGSize(width: collectionView.frame.width/5, height: collectionView.frame.height)
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int { return selectionList.count }
}