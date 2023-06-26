//
//  TabBarViewController.swift
//  ios
//
//  Created by 정호진 on 2023/06/18.
//

import Foundation
import UIKit

final class TabBarViewController: UITabBarController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let size: CGFloat = 25
        
        let questionVC = QuestionViewController()
        let rankingVC = AllUserRankingController()
        let homeVC = MainViewController()
        let compareVC = CompareChooseRepoViewController()
        let profileVC = DetailInfoController()
        
        
        questionVC.tabBarItem.image = UIImage(named: "question")?.resize(newWidth: size)
        rankingVC.tabBarItem.image = UIImage(named: "ranking")?.resize(newWidth: size)
        homeVC.tabBarItem.image = UIImage(named: "home")?.resize(newWidth: size)
        compareVC.tabBarItem.image = UIImage(named: "compare")?.resize(newWidth: size)
        profileVC.tabBarItem.image = UIImage(named: "profile")?.resize(newWidth: size)
        
        self.viewControllers = [questionVC, rankingVC, homeVC, compareVC, profileVC]
        setViewControllers(viewControllers, animated: false)
        self.selectedIndex = 2
    }

    
}
