//
//  ChooseUserViewController.swift
//  ios
//
//  Created by 정호진 on 2023/06/22.
//

import Foundation
import UIKit
import SnapKit

final class ChooseUserViewController: UIViewController{
    var beforeUser: String?
    var userList: [AllMemberInfoModel]?
    var delegate: SendUser?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addUI()
    }
    
    // MARK:
    private lazy var tableView: UITableView = {
        let table = UITableView()
        table.backgroundColor = .white
        return table
    }()
    
    
    // MARK:
    private func addUI(){
        view.addSubview(tableView)
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(ChooseUserTableViewCell.self, forCellReuseIdentifier: ChooseUserTableViewCell.identifier)
        
        tableView.snp.makeConstraints { make in
            make.top.leading.trailing.bottom.equalTo(view.safeAreaLayoutGuide)
        }
    }
    
    
    
}

extension ChooseUserViewController: UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: ChooseUserTableViewCell.identifier, for: indexPath) as? ChooseUserTableViewCell else { return UITableViewCell() }
        cell.backgroundColor = .white
        
        if let githubId = userList?[indexPath.row].github_id, let organization = userList?[indexPath.row].organization {
            
            cell.inputData(text: "\(organization.split(separator: "/")[1])/\(githubId)")
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.delegate?.sendUser(choseRepo: self.beforeUser ?? "", index: indexPath.row)
        self.dismiss(animated: true)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { return userList?.count ?? 0 }
}

protocol SendUser{
    func sendUser(choseRepo: String, index: Int)
}
