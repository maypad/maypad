import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { Branch } from 'src/app/model/branch';
import { BranchService } from 'src/app/branch.service';
import { stripGeneratedFileSuffix } from '@angular/compiler/src/aot/util';

@Component({
    selector: 'app-branch-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
    @Input() projId: number;
    @Input() branch: Branch;
    @ViewChild('rebuild') rebuild: ElementRef;
    constructor(private branchService: BranchService) { }

    ngOnInit() { }

    triggerDeploy(build: Boolean) {
        this.branchService.triggerDeployment(
            this.projId, this.branch.name, true, this.rebuild.nativeElement.checked
        ).subscribe(
            x => { },
            error => {
                console.error(error);
                alert(`Deployment couldn't be started. see console for error log.`);
            },
            () => { this.alert('deployment'); }
        );
    }

    triggerBuild() {
        this.branchService.triggerBuild(
            this.projId, this.branch.name, this.rebuild.nativeElement.checked
        ).subscribe(
            x => { },
            error => {
                console.error(error);
                alert(`Build couldn't be started. see console for error log.`);
            },
            () => { this.alert('build'); }
        );
    }

    buildAndDeploy() {
        this.triggerDeploy(true);
    }

    alert(type: string) {
        const hulla = new hullabaloo();
        hulla.options.align = 'center';
        hulla.options.width = 350;
        hulla.options.offset = { from: 'top', amount: 30 };
        hulla.send(`The ${type} has been started.`, 'info');
    }
}
