import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildHistoryComponent } from './build-history.component';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { of } from 'rxjs';
import * as buildsResponse from 'sample-requests/get.projects.id.branches.ref.builds.response.json';
import * as branchResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import * as projectResponse from 'sample-requests/get.projects.id.response.json';
import { Build } from '../model/build';
import { BhistoryListItemComponent } from './bhistory-list-item/bhistory-list-item.component';

describe('BuildHistoryComponent', () => {
  let component: BuildHistoryComponent;
  let fixture: ComponentFixture<BuildHistoryComponent>;
  const builds: Build[] = buildsResponse['default'];
  const branch = branchResponse['default'];
  const project = projectResponse['default'];
  const snapshot = new ActivatedRouteSnapshot();

  beforeEach(async(() => {
    spyOn(snapshot.paramMap, 'get').and.returnValue(null);
    TestBed.configureTestingModule({
      declarations: [BuildHistoryComponent, BhistoryListItemComponent],
      providers: [
        {
          provide: ActivatedRoute, useClass: class {
            snapshot = snapshot; data = of({ builds: builds, branch: branch, project: { ...project } });
          }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create list items', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const elements: NodeListOf<Element> = nativeElement.querySelectorAll('app-bhistory-list-item');
    expect(elements.length).toBe(builds.length);
  });
});
