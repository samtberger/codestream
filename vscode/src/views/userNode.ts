'use strict';
import { TreeItem, TreeItemCollapsibleState } from 'vscode';
import { CodeStreamSession, User } from '../api/session';
import { ExplorerNode, ResourceType } from './explorerNode';
import { Container } from '../container';

export class UserNode extends ExplorerNode {

    constructor(
        public readonly session: CodeStreamSession,
        public readonly user: User
    ) {
        super();
    }

    get id() {
        return `${this.session.id}:${ResourceType.User}:${this.user.id}`;
    }

    async getChildren(): Promise<ExplorerNode[]> {
        return [];
    }

    getTreeItem(): TreeItem {
        const item = new TreeItem(this.user.name, TreeItemCollapsibleState.None);
        item.id = this.id;
        item.contextValue = ResourceType.User;
        item.iconPath = Container.context.asAbsolutePath(`assets/images/presence-online.svg`);
        return item;
    }
}