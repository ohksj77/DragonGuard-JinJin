//
//  OrganizationDetailController.swift
//  ios
//
//  Created by 정호진 on 2023/06/17.
//

import Foundation
import UIKit
import SnapKit
import RxSwift

// MARK: 내 조직을 눌렀을 때 내부 레포지토리들 보여주는 화면
final class OrganizationDetailController: UIViewController{
    private let disposeBag = DisposeBag()
    var data: RepositoriesInOrganizationModel?
    var name: String?
    private let viewModel = RepositoriesInOrganizationViewModel()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        getData()
        
    }
    
    // MARK: back button
    private lazy var backBtn: UIButton = {
        let btn = UIButton()
        btn.setImage(UIImage(named: "backBtn")?.resize(newWidth: 30), for: .normal)
        return btn
    }()
    
    // MARK:
    private lazy var repoLabel: UILabel = {
        let label = UILabel()
        label.text = "Repository"
        label.font = UIFont(name: "IBMPlexSansKR-SemiBold", size: 30)
        return label
    }()
    
    // MARK: Oraganization Repository 보여줄 TableView
    private lazy var tableView: UITableView = {
        let table = UITableView()
        table.separatorStyle = .none
        return table
    }()
    
    // MARK:
    private func addUI(){
        view.addSubview(backBtn)
        view.addSubview(repoLabel)
        view.addSubview(tableView)
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(OrganizationDetailTableViewCell.self, forCellReuseIdentifier: OrganizationDetailTableViewCell.identifier)
            
        backBtn.snp.makeConstraints { make in
            make.top.equalTo(view.safeAreaLayoutGuide).offset(10)
            make.leading.equalTo(view.safeAreaLayoutGuide).offset(10)
        }
        
        repoLabel.snp.makeConstraints { make in
            make.top.equalTo(backBtn.snp.bottom)
            make.leading.equalTo(backBtn.snp.trailing)
        }
        
        tableView.snp.makeConstraints { make in
            make.top.equalTo(repoLabel.snp.bottom).offset(15)
            make.leading.trailing.equalTo(view.safeAreaLayoutGuide)
            make.bottom.equalTo(view.safeAreaLayoutGuide)
        }
        
        clickedBackBtn()
        
    }
    
    // MARK:
    private func clickedBackBtn(){
        backBtn.rx.tap.subscribe(onNext: {
            self.dismiss(animated: true)
        })
        .disposed(by: disposeBag)
        
    }
    
    // MARK:
    private func getData(){
        
        viewModel.getData(name: self.name ?? "")
            .subscribe(onNext:{ data in
                self.data = data
                self.addUI()
            })
            .disposed(by: disposeBag)
        
    }
    
    
}

extension OrganizationDetailController: UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: OrganizationDetailTableViewCell.identifier, for: indexPath) as? OrganizationDetailTableViewCell else {return UITableViewCell()}
        let organizationTitle = (data?.git_repos?[indexPath.row] ?? "").split(separator: "/")[0]
        let title = (data?.git_repos?[indexPath.row] ?? "").split(separator: "/")[1]
        
        cell.inputData(title: "\(title)",
                       imgPath: data?.profile_image ?? "",
                       organizationTitle: "\(organizationTitle)")
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let nextPage = RepoDetailController()
        nextPage.modalPresentationStyle = .fullScreen
        
        nextPage.selectedTitle = data?.git_repos?[indexPath.row] ?? ""
        self.present(nextPage,animated: true)
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return view.safeAreaLayoutGuide.layoutFrame.height/6
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { return data?.git_repos?.count ?? 0 }
}
