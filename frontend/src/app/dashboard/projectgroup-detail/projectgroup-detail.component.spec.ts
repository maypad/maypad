import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectgroupDetailComponent } from './projectgroup-detail.component';

describe('ProjectgroupDetailComponent', () => {
  let component: ProjectgroupDetailComponent;
  let fixture: ComponentFixture<ProjectgroupDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectgroupDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectgroupDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
