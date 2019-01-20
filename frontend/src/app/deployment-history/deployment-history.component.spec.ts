import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentHistoryComponent } from './deployment-history.component';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { of } from 'rxjs';
import * as deploymentsResponse from 'sample-requests/get.projects.id.branches.ref.deployments.response.json';
import * as branchResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import { DhistoryListItemComponent } from './dhistory-list-item/dhistory-list-item.component';
import { Deployment } from '../model/deployment';

describe('DeploymentHistoryComponent', () => {
  let component: DeploymentHistoryComponent;
  let fixture: ComponentFixture<DeploymentHistoryComponent>;
  const deployments: Deployment[] = deploymentsResponse['default'];
  const branch = branchResponse['default'];
  const snapshot = new ActivatedRouteSnapshot();

  beforeEach(async(() => {
    spyOn(snapshot.paramMap, 'get').and.returnValue(null);
    TestBed.configureTestingModule({
      declarations: [DeploymentHistoryComponent, DhistoryListItemComponent],
      providers: [
        {
          provide: ActivatedRoute, useClass: class {
            snapshot = snapshot; data = of({ deployments: deployments, branch: branch });
          }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create list items', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const elements: NodeListOf<Element> = nativeElement.querySelectorAll('app-dhistory-list-item');
    expect(elements.length).toBe(deployments.length);
  });
});
