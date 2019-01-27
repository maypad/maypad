import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { Branch } from 'src/app/model/branch';
import { BranchService } from 'src/app/branch.service';
import { stripGeneratedFileSuffix } from '@angular/compiler/src/aot/util';
import { NotificationService } from 'src/app/notification.service';

@Component({
    selector: 'app-branch-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
    @Input() projId: number;
    @Input() branch: Branch;
    @ViewChild('rebuild') rebuild: ElementRef;
    constructor(
        private branchService: BranchService,
        private notification: NotificationService
    ) { }

    ngOnInit() { }

    triggerDeploy(build: boolean) {
        this.branchService.triggerDeployment(
            this.projId, this.branch.name, build, this.rebuild.nativeElement.checked
        ).subscribe(
            x => { },
            error => {
                console.error(error);
                this.notification.send(`Deployment couldn't be started. see console for error log.`, 'danger');
            },
            () => { this.triggerAlert('deployment'); }
        );
    }

    triggerBuild() {
        this.branchService.triggerBuild(
            this.projId, this.branch.name, this.rebuild.nativeElement.checked
        ).subscribe(
            x => { },
            error => {
                console.error(error);
                this.notification.send(`Build couldn't be started. see console for error log.`, 'danger');
            },
            () => { this.triggerAlert('build'); }
        );
    }

    buildAndDeploy() {
        this.triggerDeploy(true);
    }

    triggerAlert(type: string) {
        const message = `The ${type} has been started.`;
        this.showAlert(message);
    }

    showAlert(msg: string) {
        this.notification.send(msg, 'info');
    }
}
