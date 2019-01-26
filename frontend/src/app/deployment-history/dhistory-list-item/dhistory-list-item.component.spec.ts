import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DhistoryListItemComponent } from './dhistory-list-item.component';
import * as deploymentsResponse from 'sample-requests/get.projects.id.branches.ref.deployments.response.json';
import { Deployment } from 'src/app/model/deployment';

describe('DhistoryListItemComponent', () => {
  let component: DhistoryListItemComponent;
  let fixture: ComponentFixture<DhistoryListItemComponent>;
  const deployment: Deployment = deploymentsResponse['default'][0];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DhistoryListItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DhistoryListItemComponent);
    component = fixture.componentInstance;
    component.deployment = deployment;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list deployment info', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const elements: NodeListOf<Element> = nativeElement.querySelectorAll('span');

    expect(elements[0].textContent).toBe('Type: ' + deployment.type);
  });
});
